//
//  Soda.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/19/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "Soda.h"

#import "NSObject+ToJson.h"
#import "NSDictionary+JsonObjectFromDict.h"
#import "NSString+JsonObject.h"
#import "ObjProxy.h"
#import "ObjRef.h"
#import "SodaObjectDecorator.h"
#import "MsgContainer.h"
#import <SBJson/SBJson.h>
#import <DCKeyValueObjectMapping/DCKeyValueObjectMapping.h>


@implementation Soda



-(id)init
{
    self = [super init];
    if(!self){
        return nil;
    }
    
    NSString* hostid = [[NSUUID UUID] UUIDString];
    self.host = [NSString stringWithFormat:@"soda://%@",hostid];
    self.namingService = [[NamingSvc alloc]initWithHost:self.host];
    connectLock = [[NSCondition alloc]init];
    self.connected = FALSE;
    
    backgroundQueue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    
    self.responseCatchers = [[NSMutableDictionary alloc]init];
    self.invoker = [[ObjInvoker alloc]initWithSoda:self];
    
    return self;
}

-(void) connect:(NSString*)host withListener:(id<SodaListener>)listener
{
    self.listener = listener;
    
    // if you want debug log set this to YES, default is NO
    [MDWamp setDebug:YES];
    
    self.wamp = [[MDWamp alloc] initWithUrl:host delegate:self];
    
    // set if MDWAMP should automatically try to reconnect after a network fail default YES
    [self.wamp setShouldAutoreconnect:YES];
    
    // set number of times it tries to autoreconnect after a fail
    [self.wamp setAutoreconnectMaxRetries:600];
    
    // set seconds between each reconnection try
    [self.wamp setAutoreconnectDelay:2];
    
    [self.wamp connect];
}

-(ObjRef*)bindObject:(id<SodaObject>)obj toId:(NSString*)id
{
   return [self.namingService bindObject:obj toId:id];
}

-(ObjRef*)bindObject:(id<SodaObject>)obj 
{
    return [self.namingService bindObject:obj];
}

-(ObjRef*)bindObject:(id)obj toInterface:(id<SodaObject>)interface
{
    SodaObjectDecorator* bound = [[SodaObjectDecorator alloc]initWithTarget:obj andType:interface];
    return [self.namingService bindObject:bound];
}

-(id) toObject:(ObjRef*)ref ofType:(Class)type
{
    id obj = [self.namingService getObject:ref];
    if(obj == nil){
        obj = [self createProxyWithRef:ref andType:type];
    }
    return obj;
}

-(id)createProxyWithRef:(ObjRef*)ref andType:(Class)type
{
    id proxy = [self.namingService getObject:ref];
    
    if(proxy == nil){
        ObjProxy* prox = PROXY(ref,type);
        prox.soda = self;
        proxy = prox;
        [self.namingService bindObject:proxy toRef:ref];
    }
    
    return proxy;
}

-(NSString*) marshall:(id)obj
{
    return [obj toJsonWithSoda:self];
}

-(id) unmarshall:(NSString*)json withType:(Class)type
{
    return [json toJsonObject:type];
}

-(void)disconnected
{
    self.namingService.parent = nil;
}

// network
-(void) send:(Msg*)msg
{
    msg.source = self.host;
    NSString* json = [self marshall:msg];
    NSString* dest = msg.destination;
    MsgContainer* cont = [[MsgContainer alloc]init];
    cont.destination = dest;
    cont.msg = json;
    [self.wamp publish:[cont toDict] toTopic:cont.destination excludeMe:YES];
}

-(void) awaitConnect
{
    [connectLock lock];
    
    while(self.connected == FALSE){
        [connectLock wait];
    }
    
    [connectLock unlock];
}

- (void) onOpen
{
    NamingSvc* root = [self createProxyWithRef:ROOT_NAMING_SVC_REF andType:[NamingSvc class]];
    self.namingService.parent = root;
    
    [self.wamp subscribeTopic:self.host withDelegate:self];
    
    NSLog(@"connected");
    [connectLock lock];
    
    self.connected = TRUE;
    [connectLock broadcast];
    
    [connectLock unlock];
    
    dispatch_async(backgroundQueue, ^(void) {
        [self.listener connected:self];
    });
}

- (void) onClose:(int)code reason:(NSString *)reason
{
    NSLog(@"disconnected");
    [connectLock lock];
    
    self.connected = FALSE;
    [connectLock broadcast];
    
    [connectLock unlock];
    
    dispatch_async(backgroundQueue, ^(void) {
        [self.listener disconnected];
    });

}

-(void)addResponseCatcher:(ResponseCatcher*)catcher forId:(NSString*)msgid
{
    [self.responseCatchers setObject:catcher forKey:msgid];
}

-(void)removeResponseCatcher:(NSString*)msgid
{
    [self.responseCatchers removeObjectForKey:msgid];
}

-(void)routeMsg:(NSDictionary*)attrs
{
    
    NSString* msgjson = [attrs objectForKey:@"msg"];
    Msg* msg = [msgjson toJsonObject:[Msg class]];
    
    NSString* type = msg.type;
    
    if([type isEqualToString:@"response"]){
        InvocationResponseMsg* resp = [msgjson toJsonObject:[InvocationResponseMsg class]];
        ResponseCatcher* catcher = [self.responseCatchers objectForKey:resp.responseTo];
        [self removeResponseCatcher:resp.responseTo];
        [catcher setResultFromResponse:resp];
    }
    else if([type isEqualToString:@"invocation"]){
        InvocationMsg* invoke = [msgjson toJsonObject:[InvocationMsg class]];
        InvocationResponseMsg* resp = [self.invoker invoke:invoke];
        [self send:resp];
    }
}

- (void) onEvent:(NSString *)topicUri eventObject:(id)object
{
    NSLog(@"event recv'd: [%@]",object);
    dispatch_async(backgroundQueue, ^(void) {
        NSDictionary* msg = object;
        [self routeMsg:msg];
    });
    
}

@end
