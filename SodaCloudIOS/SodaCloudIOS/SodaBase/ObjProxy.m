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

@implementation ObjProxy


- (id)initWithObjRef:(ObjRef*)ref andSodaObject:(id)spec;
{
    target = ref;
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

- (void)forwardInvocation:(NSInvocation *)anInvocation
{
    SodaMethod* method = [self methodFromSelector:anInvocation.selector];
    
    if(method != nil){
        InvocationMsg* invoke = [[InvocationMsg alloc]init];
        invoke.method = method.name;
        invoke.destination = [target getHost];
        invoke.uri = target.uri;
        invoke.parameters = [self getArgsFromInvocation:anInvocation withTotal:method.parameterTypes.count];
        
        [self sendInvocation:invoke];
        
        if(method.returnType != nil || ![method isAsyncIfVoid]){
            
            ResponseCatcher* catcher = (self.catcherFactory == nil)?
                [[ResponseCatcher alloc] initWithId:invoke.id]:
                [self.catcherFactory createCatcher:invoke];
    
            __unsafe_unretained id answer = [catcher getResult];
            [anInvocation setReturnValue:&answer];
        }
    }
    else {
        [super forwardInvocation:anInvocation];
    }
}

- (void)sendInvocation:(InvocationMsg*)msg
{
    
}

-(NSArray*)getArgsFromInvocation:(NSInvocation*)invocation withTotal:(int)count
{
    NSMutableArray* args = [[NSMutableArray alloc]init];
    for(int i = 2; i < count + 2; i++){
        id arg = [invocation getArgumentAtIndexAsObject:i];
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
        NSString* ctype = [method cTypes];
		sig=[NSMethodSignature signatureWithObjCTypes:[ctype cString]];
	}
	
	return sig;
}

@end
