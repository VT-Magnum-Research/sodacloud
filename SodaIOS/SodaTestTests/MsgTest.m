//
//  MsgTest.m
//  SodaIOS
//
//  Created by Jules White on 5/16/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "MsgTest.h"
#import "Msg.h"
#import "NSObject+ToJson.h"
#import "NSString+JsonObject.h"

@implementation MsgTest

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

- (void)testMarshalling
{
    Msg* m = [[Msg alloc] init];
    m.id = @"2";
    m.responseTo = @"3";
    m.type = @"invocation";
    m.destination = @"4";
    
    NSString* json = [m toJson];
    
    assert(json != nil);
}

@end
