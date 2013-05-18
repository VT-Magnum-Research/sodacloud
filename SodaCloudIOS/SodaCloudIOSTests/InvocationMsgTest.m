//
//  InvocationMsgTest.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/17/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "InvocationMsgTest.h"

#import <objc/runtime.h>
#import "InvocationMsg.h"
#import "ObjRef.h"
#import "NSString+JsonObject.h"
#import "NSObject+ToJson.h"
#import "NSDictionary+JsonObjectFromDict.h"
#import "NSArray+JsonObjects.h"

@implementation InvocationMsgTest

- (void)testMsgMarshallUnMarshallSymmetry
{
    InvocationMsg* m = [[InvocationMsg alloc] init];
    m.id = @"2";
    m.responseTo = @"3";
    m.type = @"invocation";
    m.destination = @"4";
    m.method = @"get";
    
    ObjRef* oref = [[ObjRef alloc]init];
    oref.uri = @"soda://foo#bar";
    
    m.parameters = [NSArray arrayWithObjects:@"foo",@"bar",[NSNumber numberWithInt:1],oref, nil];
    m.uri = @"soda://somehost#someobj";
    
    NSString* json = [m toJson];
    
    NSLog(@"JSON - %@",json);
    InvocationMsg* m2 = [json toJsonObject:[InvocationMsg class]];
    
    
    STAssertEqualObjects(m2.id, m.id, @"The ids of the marshalled and unmarshalled msg objects did not match.");
    STAssertEqualObjects(m2.responseTo, m.responseTo, @"The marshalled and unmarshalled msg objects did not match.");
    STAssertEqualObjects(m2.type, m.type, @"The marshalled and unmarshalled msg objects did not match.");
    STAssertEqualObjects(m2.destination, m.destination, @"The marshalled and unmarshalled msg objects did not match.");
    STAssertEqualObjects(m2.method, m.method, @"The marshalled and unmarshalled msg objects did not match.");
    STAssertEqualObjects(m2.uri, m.uri, @"The marshalled and unmarshalled msg objects did not match.");

    STAssertEquals(m2.parameters.count, m.parameters.count, @"The marshalled and unmarshalled msg objects did not match.");
    
    NSArray* types = [[NSArray alloc]initWithObjects:[NSString class],[NSString class],[NSNumber class],[ObjRef class], nil];
    NSArray* uparams = [m2.parameters toJsonObjectsOfTypes:types];
    
    for(int i = 0; i < m.parameters.count; i++){
        id obj = [uparams objectAtIndex:i];
        id orig = [m.parameters objectAtIndex:i];
        STAssertEqualObjects(orig, obj, @"The marshalled and unmarshalled msg objects did not match.");
    }
    
    Class clz = [ObjRef class];
    int unsigned numMethods;
    Method *methods = class_copyMethodList(clz, &numMethods);
    for (int i = 0; i < numMethods; i++) {
        NSString* name = NSStringFromSelector(method_getName(methods[i]));
        NSLog(name);
        int args = method_getNumberOfArguments(methods[i]);
        for(int j = 0; j < args; j++){
            char* aname = method_copyArgumentType(methods[i], j);
            NSLog(@"arg-%@",[NSString stringWithCString:aname]);
        }
    }
}

@end
