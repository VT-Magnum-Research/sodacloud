//
//  Soda.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/19/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ObjRef.h"
#import "MsgReceiver.h"
#import "NamingSvc.h"
#import "SodaObject.h"
#import "MDWamp.h"
#import "SodaListener.h"
#import "ObjInvoker.h"

@class ObjInvoker;
@class ResponseCatcher;

@interface Soda : NSObject<MDWampDelegate,MDWampEventDelegate>
{
    NSCondition* connectLock;
    dispatch_queue_t backgroundQueue;
}

@property(nonatomic,retain)NSMutableDictionary* responseCatchers;
@property(nonatomic,retain)ObjInvoker* invoker;
@property(nonatomic,retain)id<SodaListener> listener;
@property(nonatomic)BOOL connected;
@property(nonatomic,retain)MDWamp* wamp;
@property(nonatomic,retain)NSString* host;
@property(nonatomic,retain)NamingSvc* namingService;

// proxy / naming service methods
-(ObjRef*)bindObject:(id<SodaObject>)obj;
-(ObjRef*)bindObject:(id<SodaObject>)obj toId:(NSString*)id;
-(ObjRef*)bindObject:(id)obj toInterface:(id<SodaObject>)interface;
-(id)createProxyWithRef:(ObjRef*)ref andType:(Class)type;
-(id) toObject:(ObjRef*)ref ofType:(Class)type;

// marshalling
-(NSString*) marshall:(id)obj;
-(id) unmarshall:(NSString*)json withType:(Class)type;

// network
-(void) send:(Msg*)msg;

-(void) connect:(NSString*)host withListener:(id<SodaListener>)listener;
-(void) awaitConnect;
-(void) onOpen;
-(void) onClose:(int)code reason:(NSString *)reason;
-(void) onEvent:(NSString *)topicUri eventObject:(id)object;
-(void)addResponseCatcher:(ResponseCatcher*)catcher forId:(NSString*)msgid;
-(void)removeResponseCatcher:(NSString*)msgid;

@end
