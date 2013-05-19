//
//  ResponseCatcherFactory.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/19/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ResponseCatcher.h"
#import "InvocationMsg.h"

@protocol ResponseCatcherFactory <NSObject>

-(ResponseCatcher*)createCatcher:(InvocationMsg*)msg;

@end
