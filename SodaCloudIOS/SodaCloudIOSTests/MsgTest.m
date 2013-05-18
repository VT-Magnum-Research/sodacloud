//
//  SodaCloudIOSTests.m
//  SodaCloudIOSTests
//
//  Created by Jules White on 5/16/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "Msg.h"
#import "NSObject+ToJson.h"
#import "NSString+JsonObject.h"



#import "MsgTest.h"

@implementation SodaCloudIOSTests

- (void)setUp
{
    [super setUp];
    
    // Set-up code here.
}

- (void)tearDown
{
    // Tear-down code here.
    
    [super tearDown];
}

- (void)testMsgMarshallUnMarshallSymmetry
{
    Msg* m = [[Msg alloc] init];
    m.id = @"2";
    m.responseTo = @"3";
    m.type = @"invocation";
    m.destination = @"4";
    
    NSString* json = [m toJson];
    
    NSLog(@"JSON - %@",json);
    assert(json != nil);
    
    Msg* m2 = [json toJsonObject:[Msg class]];
    
    NSLog(@"JSON2 - %@",[m2 toJson]);
    
    STAssertEqualObjects(m2.id, m.id, @"The ids of the marshalled and unmarshalled msg objects did not match.");
    STAssertEqualObjects(m2.responseTo, m.responseTo, @"The marshalled and unmarshalled msg objects did not match.");
    STAssertEqualObjects(m2.type, m.type, @"The marshalled and unmarshalled msg objects did not match.");
    STAssertEqualObjects(m2.destination, m.destination, @"The marshalled and unmarshalled msg objects did not match.");
}

@end
