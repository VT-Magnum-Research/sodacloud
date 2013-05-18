//
//  ObjRefTest.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/17/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "ObjRefTest.h"

#import "ObjRef.h"
#import "NSObject+ToJson.h"
#import "NSString+JsonObject.h"
#import <SBJson.h>

@implementation ObjRefTest

- (void)testObjRefInit
{
    ObjRef* oref = [[ObjRef alloc] init];
    [oref setHost:@"localhost" andObjId:@"123"];
    NSString* host = [oref getHost];
    
    STAssertNotNil(oref, @"The objref was unexpectedly nil");
    STAssertNotNil(oref.uri, @"The objref did not have a uri set");
    STAssertEqualObjects(oref.uri, @"soda://localhost#123", @"The objref did not initialize the correct uri.");
    STAssertEqualObjects(host,@"soda://localhost",@"The host for the objref was not set properly");
}

- (void)testMarshalling
{
    ObjRef* oref = [[ObjRef alloc] init];
    [oref setHost:@"localhost" andObjId:@"123"];
    
    NSString* json = [oref toJson];
    ObjRef* oref2 = [json toJsonObject:[ObjRef class]];
    
    SBJsonParser *jsonParser = [[SBJsonParser alloc] init];
    NSError *error = nil;
    NSDictionary *jsonObject = [jsonParser objectWithString:json error:&error];
    
    STAssertEqualObjects([jsonObject valueForKey:@"type"], @"ObjRef", @"The objref type was not marshalled.");
    STAssertEqualObjects([jsonObject valueForKey:@"uri"], @"soda://localhost#123", @"The objref uri was not marshalled.");
    STAssertEqualObjects([jsonObject valueForKey:@"host"], @"soda://localhost", @"The objref host was not marshalled.");
    
    STAssertEqualObjects(oref.uri, oref2.uri, @"The objref did not marshall its uri correctly.");
    STAssertEqualObjects([oref getHost],[oref2 getHost], @"The host was not marshalled correctly.");
}

@end
