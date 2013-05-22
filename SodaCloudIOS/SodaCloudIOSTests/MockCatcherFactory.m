//
//  MockCatcherFactory.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/21/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "MockCatcherFactory.h"


@implementation MockCatcherFactory
-(ResponseCatcher*)createCatcher:(InvocationMsg *)msg
{
    ResponseCatcher* catch = [[ResponseCatcher alloc]init];
    [catch setResult:self.result];
    return catch;
}
@end

