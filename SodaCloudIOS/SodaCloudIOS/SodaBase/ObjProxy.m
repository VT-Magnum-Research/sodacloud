//
//  ObjProxy.m
//  SodaIOS
//
//  Created by Jules White on 5/15/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "ObjProxy.h"

#import "NSInvocation+OCMAdditions.h"
#import "InvocationMsg.h"
#import "ResponseCatcher.h"
#import "NSObject+ToJson.h"
#import "SodaPass.h"

@implementation ObjProxy


- (id)initWithObjRef:(ObjRef*)ref andSodaObject:(id)spec;
{
    _target = ref;
    interface = spec;
    waitLock = [[NSCondition alloc] init];
    return self;
}

-(SodaMethod*)methodFromSelector:(SEL)sel
{
    NSString* name = NSStringFromSelector(sel);
    name = [[name componentsSeparatedByString:@":"] objectAtIndex:0];
    SodaMethod* method = [self findMethod:name];
    return method;
}

-(id)returnTypeForMethod:(SodaMethod*)method withParams:(NSArray*)params
{
    Class type = nil;
    if(method.returnTypeFromParamIndex != -1){
        type = [params objectAtIndex:method.returnTypeFromParamIndex];
    }
    else{
        type = method.returnType;
    }
    
    return type;
}

- (void)forwardInvocation:(NSInvocation *)anInvocation
{
    SodaMethod* method = [self methodFromSelector:anInvocation.selector];
    
    if(method != nil){
        InvocationMsg* invoke = [[InvocationMsg alloc]init];
        invoke.method = method.name;
        invoke.destination = [_target getHost];
        invoke.uri = _target.uri;
        invoke.parameters = [self getArgsFromInvocation:anInvocation withMethod:method];
        
        
        // Need to think about this next block. It is possible, although very unlikely,
        // that the msg could make it across to the target and be answered before the
        // ResponseCatcher was created and ready to receive the response...
        //
        // 
        [self sendInvocation:invoke];
        
        if(![method isVoid] || ![method isAsyncIfVoid]){
            
            Class type = [method returnTypeForMethodWithInvocation:anInvocation];
            
            ResponseCatcher* catcher = (self.catcherFactory == nil)?
                [[ResponseCatcher alloc] initWithId:invoke.id andReturnType:type andSoda:self.soda]:
                [self.catcherFactory createCatcher:invoke];
            catcher.soda = self.soda;
    
            __unsafe_unretained id answer = [catcher getResult];
            [anInvocation setReturnValue:&answer];
        }
    }
    else {
        NSString* name = NSStringFromSelector(anInvocation.selector);
        if([name isEqualToString:@"hash:"]){
            [anInvocation setTarget:_target];
        }
        else if([interface respondsToSelector:anInvocation.selector]){
            [anInvocation setTarget:interface];
        }
        else {
            [super forwardInvocation:anInvocation];
        }
    }
}

- (void)sendInvocation:(InvocationMsg*)msg
{
    [self.soda send:msg];
}

-(NSArray*)getArgsFromInvocation:(NSInvocation*)invocation withMethod:(SodaMethod*)method
{
    int count = method.parameterTypes.count;
    
    NSMutableArray* args = [[NSMutableArray alloc]init];
    for(int i = 2; i < count + 2; i++){
        
        id arg = [invocation getArgumentAtIndexAsObject:i];
        
        id type = [method.parameterTypes objectAtIndex:(i-2)];
        if([type isKindOfClass:[RefParam class]] ||
           [SodaPass isByReference:type]){
            // do the magic to convert the
            // object into an obj ref;
            // arg = converted objref;
            RefParam* param = type;
            ObjRef* ref = [self.soda bindObject:arg toInterface:[[param.type alloc]init]];
            arg = ref;
        }
        
        [args addObject:arg];
    }
    
    return args;
}


-(SodaMethod*)findMethod:(NSString*)methodName
{
    SodaMethod* smethod = nil;
    
    for(SodaMethod* method in [interface sodaMethods]){
        if([methodName isEqualToString:method.name]){
            smethod = method;
            break;
        }
    }
    
    return smethod;
}

- (NSMethodSignature *)methodSignatureForSelector:(SEL)sel {
	NSMethodSignature *sig;
	sig=[[interface class] instanceMethodSignatureForSelector:sel];
	if(sig==nil)
	{
        SodaMethod* method = [self methodFromSelector:sel];
        
        if(method != nil){
            NSString* ctype = [method cTypes];
            sig=[NSMethodSignature signatureWithObjCTypes:[ctype cString]];
        }
        else {
            if(sel == @selector(hash)){
                sig = [NSMethodSignature signatureWithObjCTypes:"@^c^v"];
            }
            else if(sel == @selector(copyWithZone:)){
                 sig = [NSMethodSignature signatureWithObjCTypes:"@^c^v@"];
            }
        }
	}
	
	return sig;
}

-(BOOL)isKindOfClass:(Class)aClass
{
    return [interface isKindOfClass:aClass] || [aClass isSubclassOfClass:[ObjProxy class]];
}

-(NSUInteger)hash
{
    return [_target hash];
}

- (id)copyWithZone:(NSZone *)zone
{
    ObjProxy *copy = [[[self class] alloc] initWithObjRef:_target andSodaObject:interface];
    return copy;
}

@end
