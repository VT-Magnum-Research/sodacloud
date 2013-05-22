//
//  MockCatcherFactory.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/21/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "InvocationMsg.h"
#import "ResponseCatcher.h"
#import "ResponseCatcherFactory.h"

@interface MockCatcherFactory : NSObject<ResponseCatcherFactory>
-(ResponseCatcher*)createCatcher:(InvocationMsg *)msg;
@property(nonatomic,assign) id result;
@end
