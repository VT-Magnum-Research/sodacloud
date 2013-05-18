//
//  PingSvcImpl.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/17/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "PingSvc.h"

@interface PingSvcImpl : NSObject<PingSvc>

-(void)ping;
-(NSDictionary*)typeMappings;

@end
