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
#import "SodaObjectDecorator.h"
#import "NSArray+JsonObjects.h"

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
    resp.destination = msg.source;
    resp.responseTo = msg.id;
    
    @try{
        SodaMethod* method = [self findMethod:msg.method inObj:target];
        SEL tocall = [self findTargetMethod:msg.method in:target];
        
        NSMethodSignature* sig = [target methodSignatureForSelector:tocall];
        NSInvocation* inv = [NSInvocation invocationWithMethodSignature:sig];
        inv.selector = tocall;
        inv.target = target;
        
        NSArray* paramTypes = [method parameterTypes];
        
        Method m = class_getInstanceMethod([target class], tocall);

        if(m == nil){
            m = class_getClassMethod([target class], tocall);
        }
        
        if(m == nil && [target isKindOfClass:[SodaObjectDecorator class]]){
            m = class_getInstanceMethod([[target getTarget] class], tocall);
            
            if(m == nil){
                m = class_getClassMethod([[target getTarget] class], tocall);
            }
        }
        
        
        //count includes self and selector so we need
        //to decrement by 2
        int count = method_getNumberOfArguments(m);
        if((count - 2) != paramTypes.count){
            [NSException raise:@"Invalid method signature for SODA_METHOD" format:@"%@ has an invalid SODA_METHODS signature for method %@ and should have %i params rather than %i",
                [target class],msg.method,count,paramTypes.count];
        }
        
        NSArray* params = [self unmarshallParams:msg.parameters usingTypes:paramTypes];
        for (int i = 0; i < params.count; i++) {
            NSObject* val = [params objectAtIndex:i];
            [inv setArgument:&val atIndex:(i+2)];
        }
        
        [inv invoke];
        
        if(method.returnType != nil){
            __unsafe_unretained id rval = resp.result;
            [inv getReturnValue:&rval];
            resp.result = rval;
        }
    }
    @catch (NSException* e) {
        resp.exception = [e reason];
    }
    
    return resp;
}

-(NSArray*)unmarshallParams:(NSArray*)params usingTypes:(NSArray*)types
{
    return [params toJsonObjectsOfTypes:types andSoda:self.soda];
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
    
    Class clz = ([targetObject isKindOfClass:[SodaObjectDecorator class]])?
        [[targetObject getTarget] class] :
        [targetObject class];
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
