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

@interface Soda : NSObject
{}

@property(nonatomic,retain)NSString* host;
@property(nonatomic,retain)NamingSvc* namingService;

// proxy / naming service methods
-(ObjRef*)bindObject:(id<SodaObject>)obj;
-(ObjRef*)bindObject:(id<SodaObject>)obj toId:(NSString*)id;
-(id)createProxyWithRef:(ObjRef*)ref andType:(Class)type;
-(id) toObject:(ObjRef*)ref ofType:(Class)type;

// marshalling
-(NSString*) marshall:(id)obj;
-(id) unmarshall:(NSString*)json withType:(Class)type;

// network
-(void) send:(Msg*)msg;
-(void) addMsgReceiver:(id<MsgReceiver>)receiver;
-(void) removeMsgReceiver:(id<MsgReceiver>)receiver;

@end
