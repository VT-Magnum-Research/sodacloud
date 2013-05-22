//
//  ResponseCatcherTest.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/21/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "ResponseCatcherTest.h"
#import "InvocationResponseMsg.h"
#import "NSString+JsonObject.h"
#import "ResponseCatcher.h"
#import "MockSvc.h"
#import "ObjProxy.h"
#import "MockCatcherFactory.h"

@implementation ResponseCatcherTest

-(void)testHandleInvocationResponseMsgWithObjRef
{
    NSString* json = @"{\"id\":\"d72ab8cb-a23b-4256-b7b5-aabf2387a368\",\"result\":{\"host\":\"soda://b5fe9dc4-d9dc-412a-9cad-f9185522d43c\",\"interface\":[\"ping/0\",\"ping/1\",\"pingMe/1\"],\"type\":\"ObjRef\",\"uri\":\"soda://b5fe9dc4-d9dc-412a-9cad-f9185522d43c#d976736d-3de7-4025-8afc-832fb9e3d7c3\"},\"source\":\"soda://b5fe9dc4-d9dc-412a-9cad-f9185522d43c\",\"type\":\"response\",\"destination\":\"soda://342db964-741c-4db1-bf60-588323b20828\",\"responseTo\":\"76e32dbe-5722-434d-8dfe-b3412dc56225\"}";
    
    InvocationResponseMsg* msg = [json toJsonObject:[InvocationResponseMsg class]];
    
    ResponseCatcher* catcher = [[ResponseCatcher alloc] initWithId:@"foo" andReturnType:[MockSvc class]];
    catcher.soda = [[Soda alloc]init];
    
    
    [catcher setResultFromResponse:msg];
    MockSvc* svc = [catcher getResult];
    
    STAssertNotNil(svc, @"Unable to create proxy from invocation response msg using response catcher.");
    
    MockCatcherFactory* cf = [[MockCatcherFactory alloc] init];
    cf.result = svc;
    
    [(ObjProxy*)svc setCatcherFactory:cf];
    id svc2 = [svc getTest];
    
    STAssertEqualObjects(svc, svc2, @"The catcher did not return a valid proxy.");
}

@end
