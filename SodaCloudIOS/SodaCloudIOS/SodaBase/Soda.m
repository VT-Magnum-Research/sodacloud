//
//  Soda.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/19/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "Soda.h"

#import "NSObject+ToJson.h"
#import "NSString+JsonObject.h"
#import "ObjProxy.h"
#import "ObjRef.h"

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
    
    return self;
}

-(ObjRef*)bindObject:(id<SodaObject>)obj toId:(NSString*)id
{
   return [self.namingService bindObject:obj toId:id];
}

-(ObjRef*)bindObject:(id<SodaObject>)obj 
{
    return [self.namingService bindObject:obj];
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

-(void)connected
{
    NamingSvc* root = [self createProxyWithRef:ROOT_NAMING_SVC_REF andType:[NamingSvc class]];
    self.namingService.parent = root;
}

-(void)disconnected
{
    self.namingService.parent = nil;
}

// network
-(void) send:(Msg*)msg
{
    
}
-(void) addMsgReceiver:(id<MsgReceiver>)receiver
{
    
}
-(void) removeMsgReceiver:(id<MsgReceiver>)receiver
{
    
}

@end
