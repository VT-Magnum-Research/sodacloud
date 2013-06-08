//
//  MockSodaSvc.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/21/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "MockSvc.h"

@implementation MockSvc

SODA_METHODS(
             SODA_METHOD(@"foo", NSString, PARAM(NSString)),
             SODA_VOID_METHOD(@"doIt", PARAM(NSNumber),PARAM(NSString),PARAM(MockSvc)),
             SODA_NOARG_METHOD(@"getTest", MockSvc),
             SODA_VOID_METHOD(@"setTest",REF(MockSvc)),
             SODA_METHOD(@"doItAndReturnParams",NSDictionary,PARAM(NSNumber),PARAM(NSString),PARAM(MockSvc)),
             SODA_METHOD_RETURN_TYPE_FROM_PARAM(@"get", 1, PARAM(NSString))
             )
@end

