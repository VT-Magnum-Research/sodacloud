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

@interface Soda : NSObject
{}

@property(nonatomic,retain)NSString* host;
@property(nonatomic,retain)NamingSvc* namingService;

-(id)createProxyWithRef:(ObjRef*)ref andType:(Class)type;
@end
