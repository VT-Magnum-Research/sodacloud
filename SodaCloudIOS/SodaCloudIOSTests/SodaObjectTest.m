//
//  SodaObjectTest.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/18/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "SodaObjectTest.h"

#import "SodaObject.h"

@interface MockSodaObject : NSObject<SodaObject>

@end
@implementation MockSodaObject

SODA_METHODS(
    SODA_NOARG_METHOD(@"getName", NSString),
    SODA_METHOD(@"calculate", NSNumber, PARAM(NSNumber), PARAM(NSNumber)),
    SODA_VOID_METHOD(@"calculateAndSet", PARAM(NSNumber), PARAM(NSNumber)),
    SODA_VOID_METHOD(@"setFoo",PARAM(NSString)),
    SODA_VOID_NOARG_METHOD(@"setBar")
)

@end

@implementation SodaObjectTest


- (void)testSodaObjectMethodsDef
{
    MockSodaObject* obj = [[MockSodaObject alloc]init];
    NSArray* methods = [obj sodaMethods];
    
    SodaMethod* m = [methods objectAtIndex:0];
    STAssertEqualObjects(m.name, @"getName", @"The method name was not set correctly.");
    STAssertEqualObjects(m.returnType, [NSString class], @"The method return type was not set correctly.");
    STAssertTrue(m.parameterTypes.count == 0, @"The method parameter types were not set correctly.");
    
    SodaMethod* m2 = [methods objectAtIndex:1];
    STAssertEqualObjects(m2.name, @"calculate", @"The method name was not set correctly.");
    STAssertEqualObjects(m2.returnType, [NSNumber class], @"The method return type was not set correctly.");
    STAssertEqualObjects([m2.parameterTypes objectAtIndex:0],[NSNumber class], @"The method parameter types were not set correctly.");
    STAssertEqualObjects([m2.parameterTypes objectAtIndex:1],[NSNumber class], @"The method parameter types were not set correctly.");
    
    SodaMethod* m3 = [methods objectAtIndex:2];
    STAssertEqualObjects(m3.name, @"calculateAndSet", @"The method name was not set correctly.");
    STAssertEqualObjects(m3.returnType, nil, @"The method return type was not set correctly.");
    STAssertEqualObjects([m3.parameterTypes objectAtIndex:0],[NSNumber class], @"The method parameter types were not set correctly.");
    STAssertEqualObjects([m3.parameterTypes objectAtIndex:1],[NSNumber class], @"The method parameter types were not set correctly.");
    
    SodaMethod* m4 = [methods objectAtIndex:3];
    STAssertEqualObjects(m4.name, @"setFoo", @"The method name was not set correctly.");
    STAssertEqualObjects(m4.returnType, nil, @"The method return type was not set correctly.");
    STAssertEqualObjects([m4.parameterTypes objectAtIndex:0],[NSString class], @"The method parameter types were not set correctly.");
    
    SodaMethod* m5 = [methods objectAtIndex:4];
    STAssertEqualObjects(m5.name, @"setBar", @"The method name was not set correctly.");
    STAssertEqualObjects(m5.returnType, nil, @"The method return type was not set correctly.");
    STAssertTrue(m5.parameterTypes.count == 0, @"The method parameter types were not set correctly.");
}

@end
