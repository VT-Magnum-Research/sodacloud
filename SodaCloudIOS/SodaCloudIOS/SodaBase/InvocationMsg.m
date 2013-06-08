//
//  InvocationMsg.m
//  SodaIOS
//
//  Created by Jules White on 5/16/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "InvocationMsg.h"

#import <objc/runtime.h>

@implementation InvocationMsg

-(id)init
{
    self = [super init];
    if(!self){
        return nil;
    }
    
    self.type = @"invocation";
    
    return self;
}
//-(NSArray*)getMethodParamTypes:(id)targetObject
//{
//    Method mthd = [self findTargetMethod:targetObject];
//    int args = method_getNumberOfArguments(mthd);
//    NSMutableArray* argtypes = [[NSMutableArray alloc]initWithCapacity:args];
//    for (int i = 0; i < args; i++) {
//        method_copyArgumentType(<#Method m#>, <#unsigned int index#>)
//    }
//    
//}
//
//-(Method)findTargetMethod:(id)targetObject
//{
//    Class clz = [targetObject class];
//    Method rslt = nil;
//    
//    NSMethodSignature sig = [targetObject methodSignatureForSelector:<#(SEL)#>];
//    [sig ]
//    
//    int unsigned numMethods;
//    Method *methods = class_copyMethodList(clz, &numMethods);
//    for (int i = 0; i < numMethods; i++) {
//        NSString* name = NSStringFromSelector(method_getName(methods[i]));
//        name = [[name componentsSeparatedByString:@":"] objectAtIndex:0];
//        if([self.method isEqualToString:name]){
//            rslt = methods[i];
//            break;
//        }
//    }
//    
//    return rslt;
//}

@end
