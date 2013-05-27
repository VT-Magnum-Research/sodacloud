//
//  Soda.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/19/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ObjRef.h"
#import "NamingSvc.h"
#import "SodaObject.h"

@interface Soda : NSObject
{}

@property(nonatomic,retain)NSString* host;
@property(nonatomic,retain)NamingSvc* namingService;

-(ObjRef*)bindObject:(id<SodaObject>)obj;
-(ObjRef*)bindObject:(id<SodaObject>)obj toId:(NSString*)id;
-(id)createProxyWithRef:(ObjRef*)ref andType:(Class)type;
-(NSString*) marshall:(id)obj;
-(id) unmarshall:(NSString*)json withType:(Class)type;
-(id) toObject:(ObjRef*)ref ofType:(Class)type;
@end
