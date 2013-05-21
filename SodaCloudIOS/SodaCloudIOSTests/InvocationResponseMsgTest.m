//
//  InvocationResponseMsgTest.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/21/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "InvocationResponseMsgTest.h"

#import "InvocationResponseMsg.h"
#import "NSString+JsonObject.h"
#import "NSDictionary+JsonObjectFromDict.h"
#import "ObjRef.h"

@implementation InvocationResponseMsgTest

-(void)testUnmarshalling
{
    NSString* json = @"{\"id\":\"d72ab8cb-a23b-4256-b7b5-aabf2387a368\",\"result\":{\"host\":\"soda://b5fe9dc4-d9dc-412a-9cad-f9185522d43c\",\"interface\":[\"ping/0\",\"ping/1\",\"pingMe/1\"],\"type\":\"ObjRef\",\"uri\":\"soda://b5fe9dc4-d9dc-412a-9cad-f9185522d43c#d976736d-3de7-4025-8afc-832fb9e3d7c3\"},\"source\":\"soda://b5fe9dc4-d9dc-412a-9cad-f9185522d43c\",\"type\":\"response\",\"destination\":\"soda://342db964-741c-4db1-bf60-588323b20828\",\"responseTo\":\"76e32dbe-5722-434d-8dfe-b3412dc56225\"}";
    
    InvocationResponseMsg* msg = [json toJsonObject:[InvocationResponseMsg class]];

    STAssertEqualObjects(msg.id, @"d72ab8cb-a23b-4256-b7b5-aabf2387a368", @"The msg id was not unmarshalled properly");
    STAssertEqualObjects(msg.source, @"soda://b5fe9dc4-d9dc-412a-9cad-f9185522d43c", @"The msg source was not unmarshalled properly");
    STAssertEqualObjects(msg.type, @"response", @"The msg type was not unmarshalled properly");
    STAssertEqualObjects(msg.responseTo, @"76e32dbe-5722-434d-8dfe-b3412dc56225", @"The msg responseTo was not unmarshalled properly");
    STAssertEqualObjects(msg.destination, @"soda://342db964-741c-4db1-bf60-588323b20828", @"The msg destination was not unmarshalled properly");
    
    STAssertTrue([msg.result isKindOfClass:[NSDictionary class]], @"The msg result was not unmarshalled poperly");
    
    STAssertEqualObjects([msg.result objectForKey:@"host"], @"soda://b5fe9dc4-d9dc-412a-9cad-f9185522d43c", @"The result was not unmarshalled correctly.");
    STAssertTrue([[msg.result objectForKey:@"interface"] isKindOfClass:[NSArray class]], @"The result was not unmarshalled correctly.");
    STAssertEqualObjects([[msg.result objectForKey:@"interface"] objectAtIndex:1], @"ping/1", @"The result was not unmarshalled correctly.");
    
    ObjRef* ref = [msg.result toJsonObjectFromDict:[ObjRef class]];
    STAssertNotNil(ref, @"Unable to unmarshall object ref from result");
    STAssertEqualObjects(ref.uri, @"soda://b5fe9dc4-d9dc-412a-9cad-f9185522d43c#d976736d-3de7-4025-8afc-832fb9e3d7c3", @"The result was not unmarshalled correctly.");
}

@end
