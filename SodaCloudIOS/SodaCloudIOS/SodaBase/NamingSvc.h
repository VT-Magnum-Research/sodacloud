//
//  NamingSvc.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/20/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ObjRef.h"
#import "SodaObject.h"


#define META_CHANNEL @"soda://meta"
#define ROOT_NAMING_SVC_NAME @"soda://meta#naming"
#define ROOT_NAMING_SVC_REF [[ObjRef alloc]initWithUri:ROOT_NAMING_SVC_NAME]

@interface NamingSvc : NSObject<SodaObject>
{
    
}

@property(nonatomic, retain)NamingSvc* parent;
@property(nonatomic, retain)NSString* host;
@property(nonatomic, retain)NSMutableDictionary* bindings;
@property(nonatomic, retain)NSMutableDictionary* refs;

-(id)initWithHost:(NSString*)host;
-(void)bindObject:(id)obj toRef:(ObjRef*)ref;
-(ObjRef*)bindObject:(id)obj toId:(NSString*)id;
-(ObjRef*)bindObject:(id)obj;
-(id)getObject:(ObjRef*)ref;
-(id)get:(NSString*)name;
@end
