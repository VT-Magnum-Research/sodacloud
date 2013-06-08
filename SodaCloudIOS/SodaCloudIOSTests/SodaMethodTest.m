//
//  SodaMethodTest.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/18/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "SodaMethodTest.h"

#import "SodaMethod.h"


@implementation SodaMethodTest

- (void)testSodaMethodAndMacros
{
    SodaMethod* m = [[SodaMethod alloc] initWithName:@"foo" andReturnType:[NSString class], [NSString class],[NSNumber class],nil];
    
    STAssertEqualObjects(m.name, @"foo", @"The method name was not set correctly.");
    STAssertEqualObjects(m.returnType, [NSString class], @"The method return type was not set correctly.");
    
    STAssertEqualObjects([m.parameterTypes objectAtIndex:0], [NSString class], @"The method parameter types were not set correctly.");
    STAssertEqualObjects([m.parameterTypes objectAtIndex:1], [NSNumber class], @"The method parameter types were not set correctly.");
    
    
    m = SODA_METHOD_RETURN_TYPE_FROM_PARAM(@"get", 1, PARAM(NSString));
    STAssertEqualObjects(m.name, @"get", @"The method name was not set correctly.");
    STAssertEqualObjects([m.parameterTypes objectAtIndex:0], [NSString class], @"The method parameter types were not set correctly.");
    
//    NSInvocation* inv = [NSInvocation invocationWithMethodSignature:<#(NSMethodSignature *)#>];
//    inv.selector = NSSelectorFromString(@"get");
//    [inv setArgument:@"asdf" atIndex:2];
//    [inv setArgument:[SodaMethodTest class] atIndex:3];
//    STAssertEqualObjects([m returnTypeForMethodWithInvocation:inv], [SodaMethodTest class], @"The method return type was not set correctly.");
//    
    
    
    m = SODA_METHOD(@"foo", NSString, PARAM(NSString), PARAM(NSNumber), PARAM(SodaMethodTest));
    
    STAssertEqualObjects(m.name, @"foo", @"The method name was not set correctly.");
    STAssertEqualObjects(m.returnType, [NSString class], @"The method return type was not set correctly.");
    
    STAssertEqualObjects([m.parameterTypes objectAtIndex:0], [NSString class], @"The method parameter types were not set correctly.");
    STAssertEqualObjects([m.parameterTypes objectAtIndex:1], [NSNumber class], @"The method parameter types were not set correctly.");
    STAssertEqualObjects([m.parameterTypes objectAtIndex:2], [SodaMethodTest class], @"The method parameter types were not set correctly.");
    STAssertEqualObjects(@"@^v^c@@@", [m cTypes], @"The method signature was not encoded properly into a ctype.");
    
    
    m = SODA_NOARG_METHOD(@"foo", NSString);
    
    STAssertEqualObjects(m.name, @"foo", @"The method name was not set correctly.");
    STAssertEqualObjects(m.returnType, [NSString class], @"The method return type was not set correctly.");
    
    STAssertTrue(m.parameterTypes.count == 0, @"The method parameter types were not set correctly.");
    STAssertEqualObjects(@"@^v^c", [m cTypes], @"The method signature was not encoded properly into a ctype.");
    
    
    m = SODA_VOID_METHOD(@"foo", PARAM(NSString), PARAM(NSNumber), PARAM(SodaMethodTest));
    
    STAssertEqualObjects(m.name, @"foo", @"The method name was not set correctly.");
    STAssertEqualObjects(m.returnType, nil, @"The method return type was not set correctly.");
    
    STAssertEqualObjects([m.parameterTypes objectAtIndex:0], [NSString class], @"The method parameter types were not set correctly.");
    STAssertEqualObjects([m.parameterTypes objectAtIndex:1], [NSNumber class], @"The method parameter types were not set correctly.");
    STAssertEqualObjects([m.parameterTypes objectAtIndex:2], [SodaMethodTest class], @"The method parameter types were not set correctly.");
    STAssertEqualObjects(@"v^v^c@@@", [m cTypes], @"The method signature was not encoded properly into a ctype.");
    
    m = SODA_VOID_NOARG_METHOD(@"foo");
    
    STAssertEqualObjects(m.name, @"foo", @"The method name was not set correctly.");
    STAssertEqualObjects(m.returnType, nil, @"The method return type was not set correctly.");
    
    STAssertTrue(m.parameterTypes.count == 0, @"The method parameter types were not set correctly.");
    STAssertEqualObjects(@"v^v^c", [m cTypes], @"The method signature was not encoded properly into a ctype.");
}

@end
