//
//  SodaTest.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/27/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "SodaTest.h"

#import "Soda.h"
#import "MockSvc.h"
#import "MockSvcImpl.h"
#import "NSString+JsonObject.h"

@implementation SodaTest

-(void) testMarshall
{
    Soda* soda = [[Soda alloc]init];
    MockSvc* svc = [[MockSvcImpl alloc]init];
    ObjRef* ref = [soda bindObject:svc toId:@"svc"];
    
    NSString* json = [soda marshall:svc];
    NSString* refjson = [soda marshall:ref];
    
    STAssertEqualObjects(refjson, json, @"Soda did not marshall a SodaObject into an ObjRef");
}


@end
