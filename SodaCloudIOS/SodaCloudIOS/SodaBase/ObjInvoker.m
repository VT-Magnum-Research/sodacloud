//
//  ObjInvoker.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/21/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "ObjInvoker.h"

#import <objc/objc-runtime.h>
#import "SodaMethod.h"
#import "SodaObject.h"

@implementation ObjInvoker

-(id)initWithSoda:(Soda*)soda
{
    self = [super init];
    if(!self){
        return nil;
    }
    
    self.soda = soda;
    
    return self;
}


-(InvocationResponseMsg*)invoke:(InvocationMsg*)msg
{
    NSString* targetname = msg.uri;
    ObjRef* ref = [[ObjRef alloc]initWithUri:targetname];
    id target = [self.soda.namingService getObject:ref];
    
    InvocationResponseMsg* resp = [[InvocationResponseMsg alloc]init];
    
    @try{
        SodaMethod* method = [self findMethod:msg.method inObj:target];
        NSArray* paramTypes = [method parameterTypes];
        SEL tocall = [self findTargetMethod:msg.method in:target];
        
        NSMethodSignature* sig = [target methodSignatureForSelector:tocall];
        NSInvocation* inv = [NSInvocation invocationWithMethodSignature:sig];
        inv.selector = tocall;
        inv.target = target;
        
        NSArray* params = [self unmarshallParams:msg.parameters usingTypes:paramTypes];
        for (int i = 0; i < params.count; i++) {
            NSObject* val = [params objectAtIndex:i];
            [inv setArgument:&val atIndex:(i+2)];
        }
        
        [inv invoke];
        __unsafe_unretained id rval = resp.result;
        [inv getReturnValue:&rval];
        
        resp.result = rval;
    }
    @catch (NSException* e) {
        resp.exception = [e reason];
    }
    
    return resp;
}

-(NSArray*)unmarshallParams:(NSArray*)params usingTypes:(NSArray*)types
{
    return params;
}

-(SodaMethod*)findMethod:(NSString*)methodName inObj:(id)target
{
    SodaMethod* smethod = nil;
    
    for(SodaMethod* method in [target sodaMethods]){
        if([methodName isEqualToString:method.name]){
            smethod = method;
            break;
        }
    }
    
    return smethod;
}

-(SEL)findTargetMethod:(NSString*)methodName in:(id)targetObject
{
    Class clz = [targetObject class];
    Method rslt = nil;
    
    int unsigned numMethods;
    Method *methods = class_copyMethodList(clz, &numMethods);
    for (int i = 0; i < numMethods; i++) {
        NSString* name = NSStringFromSelector(method_getName(methods[i]));
        name = [[name componentsSeparatedByString:@":"] objectAtIndex:0];
        if([methodName isEqualToString:name]){
            rslt = methods[i];
            break;
        }
    }
    
    SEL method = (rslt != nil)? method_getName(rslt) : nil;
    
    return method;
}

@end
