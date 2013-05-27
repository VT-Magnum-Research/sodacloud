//
//  ObjInvokerTest.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/27/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "ObjInvokerTest.h"

#import "NSObject+ToJson.h"
#import "NSString+JsonObject.h"
#import "ObjInvoker.h"
#import "Soda.h"
#import "InvocationMsg.h"
#import "InvocationResponseMsg.h"
#import "MockSvcImpl.h"
#import "MockSvc.h"

@implementation ObjInvokerTest

-(void) testUnmarshallMsg
{
    Soda* soda = [[Soda alloc]init];
    MockSvc* svc = [[MockSvcImpl alloc]init];
    [soda bindObject:svc toId:@"svc"];
    
    ObjInvoker* invoker = [[ObjInvoker alloc] initWithSoda:soda];
    
    InvocationMsg* msg = [[InvocationMsg alloc]init];
    msg.uri = [NSString stringWithFormat:@"%@#%@",soda.host,@"svc"];
    msg.parameters = [[NSArray alloc] initWithObjects:@(5),@"test123",svc, nil];
    msg.method = @"doItAndReturnParams";
    
    NSString* json = [soda marshall:msg];
    InvocationMsg* msg2 = [json toJsonObject:[InvocationMsg class]];
    
    SodaMethod* method = [invoker findMethod:msg2.method inObj:svc];
    NSArray* params = [invoker unmarshallParams:msg2.parameters usingTypes:method.parameterTypes];
    
    for(int i = 0; i < msg.parameters.count; i++){
        id o1 = [params objectAtIndex:i];
        id o2 = [msg.parameters objectAtIndex:i];
        STAssertEqualObjects(o1, o2, @"the objinvoker did not unmarshall the parameters correctly");
    }
}

-(void) testObjInvocationSingleStringParamAndStringReturn
{
    Soda* soda = [[Soda alloc]init];
    MockSvc* svc = [[MockSvcImpl alloc]init];
    [soda bindObject:svc toId:@"svc"];
    
    ObjInvoker* invoker = [[ObjInvoker alloc] initWithSoda:soda];
    
    InvocationMsg* msg = [[InvocationMsg alloc]init];
    msg.uri = [NSString stringWithFormat:@"%@#%@",soda.host,@"svc"];
    msg.parameters = [[NSArray alloc] initWithObjects:@"test123", nil];
    msg.method = @"foo";
    
    NSString* json = [soda marshall:msg];
    msg = [soda unmarshall:json withType:[InvocationMsg class]];
    
    InvocationResponseMsg* response = [invoker invoke:msg];
    STAssertNotNil(response, @"obj invocation failed to generate a response from the objinvoker");
    STAssertEqualObjects(@"test123", response.result, @"obj invocation produced an incorrect response");
}

-(void) testObjInvocationMultipleParamsAndObjectReturn
{
    Soda* soda = [[Soda alloc]init];
    MockSvc* svc = [[MockSvcImpl alloc]init];
    [soda bindObject:svc toId:@"svc"];
    
    ObjInvoker* invoker = [[ObjInvoker alloc] initWithSoda:soda];
    
    InvocationMsg* msg = [[InvocationMsg alloc]init];
    msg.uri = [NSString stringWithFormat:@"%@#%@",soda.host,@"svc"];
    msg.parameters = [[NSArray alloc] initWithObjects:@(5),@"test123",svc, nil];
    msg.method = @"doItAndReturnParams";
    
    NSString* json = [soda marshall:msg];
    msg = [soda unmarshall:json withType:[InvocationMsg class]];
    
    InvocationResponseMsg* response = [invoker invoke:msg];
    STAssertNotNil(response, @"obj invocation failed to generate a response from the objinvoker");
    
    NSDictionary* rslt = response.result;
    STAssertNotNil(rslt, @"obj invocation failed to generate a return val from the objinvoker");
    STAssertEqualObjects([rslt objectForKey:@"a"],@(5),@"obj invocation failed returned the wrong return vals");
    STAssertEqualObjects([rslt objectForKey:@"b"],@"test123",@"obj invocation failed returned the wrong return vals");
    STAssertEqualObjects([rslt objectForKey:@"test"],svc,@"obj invocation failed returned the wrong return vals");
}

-(void) testObjInvocationWithUnboundSodaObjectAndObjectReturn
{
    Soda* soda = [[Soda alloc]init];
    MockSvc* svc = [[MockSvcImpl alloc]init];
    MockSvc* svc2 = [[MockSvcImpl alloc]init];
    [soda bindObject:svc toId:@"svc"];
    
    ObjInvoker* invoker = [[ObjInvoker alloc] initWithSoda:soda];
    
    InvocationMsg* msg = [[InvocationMsg alloc]init];
    msg.uri = [NSString stringWithFormat:@"%@#%@",soda.host,@"svc"];
    msg.parameters = [[NSArray alloc] initWithObjects:@(5),@"test123",svc2, nil];
    msg.method = @"doItAndReturnParams";
    
    NSString* json = [soda marshall:msg];
    msg = [soda unmarshall:json withType:[InvocationMsg class]];
    
    InvocationResponseMsg* response = [invoker invoke:msg];
    STAssertNotNil(response, @"obj invocation failed to generate a response from the objinvoker");
    
    NSDictionary* rslt = response.result;
    STAssertNotNil(rslt, @"obj invocation failed to generate a return val from the objinvoker");
    STAssertEqualObjects([rslt objectForKey:@"a"],@(5),@"obj invocation failed returned the wrong return vals");
    STAssertEqualObjects([rslt objectForKey:@"b"],@"test123",@"obj invocation failed returned the wrong return vals");
    STAssertEqualObjects([rslt objectForKey:@"test"],svc2,@"obj invocation failed returned the wrong return vals");
}


-(void) testObjInvocationWithProxyObjectAndObjectReturn
{
    Soda* soda = [[Soda alloc]init];
    ObjRef* dummy = [[ObjRef alloc] initWithUri:@"soda://dummy#ref"];
    MockSvc* svc = [soda toObject:dummy ofType:[MockSvc class]];

    MockSvc* svc2 = [[MockSvcImpl alloc]init];
    [soda bindObject:svc2 toId:@"svc"];
    
    ObjInvoker* invoker = [[ObjInvoker alloc] initWithSoda:soda];
    
    InvocationMsg* msg = [[InvocationMsg alloc]init];
    msg.uri = [NSString stringWithFormat:@"%@#%@",soda.host,@"svc"];
    msg.parameters = [[NSArray alloc] initWithObjects:@(5),@"test123",svc, nil];
    msg.method = @"doItAndReturnParams";
    
    NSString* json = [soda marshall:msg];
    msg = [soda unmarshall:json withType:[InvocationMsg class]];
    
    InvocationResponseMsg* response = [invoker invoke:msg];
    STAssertNotNil(response, @"obj invocation failed to generate a response from the objinvoker");
    
    NSDictionary* rslt = response.result;
    STAssertNotNil(rslt, @"obj invocation failed to generate a return val from the objinvoker");
    STAssertEqualObjects([rslt objectForKey:@"a"],@(5),@"obj invocation failed returned the wrong return vals");
    STAssertEqualObjects([rslt objectForKey:@"b"],@"test123",@"obj invocation failed returned the wrong return vals");
    STAssertEqualObjects([rslt objectForKey:@"test"],svc,@"obj invocation failed returned the wrong return vals");
}


-(void) testObjInvocationMultipleParamsAndVoidReturn
{
    Soda* soda = [[Soda alloc]init];
    MockSvc* svc = [[MockSvcImpl alloc]init];
    [soda bindObject:svc toId:@"svc"];
    
    ObjInvoker* invoker = [[ObjInvoker alloc] initWithSoda:soda];
    
    InvocationMsg* msg = [[InvocationMsg alloc]init];
    msg.uri = [NSString stringWithFormat:@"%@#%@",soda.host,@"svc"];
    msg.parameters = [[NSArray alloc] initWithObjects:@(5),@"test123",svc, nil];
    msg.method = @"doIt";
    
    NSString* json = [soda marshall:msg];
    msg = [json toJsonObject:[InvocationMsg class]];
    
    InvocationResponseMsg* response = [invoker invoke:msg];
    STAssertNotNil(response, @"obj invocation failed to generate a response from the objinvoker");
    
    NSDictionary* rslt = response.result;
    STAssertNil(rslt, @"obj invocation failed to generate a void return from the objinvoker");
}


@end
