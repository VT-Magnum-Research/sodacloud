//
//  ResponseCatcher.m
//  SodaIOS
//
//  Created by Jules White on 5/15/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "ResponseCatcher.h"

#import "NSDictionary+JsonObjectFromDict.h"

@implementation ResponseCatcher

-(id)initWithId:(NSString *)id andReturnType:(Class)type{
    waitLock = [[NSCondition alloc] init];
    self.returnType = type;
    return self;
}

-(void)setResultFromResponse:(InvocationResponseMsg*)response
{
    id rslt = nil;
    if(response.result){
        
        if([@"ObjRef" isEqualToString:[response.result objectForKey:@"type"]]){
            ObjRef* ref = [response.result toJsonObjectFromDict:[ObjRef class]];
            rslt = [self.soda createProxyWithRef:ref andType:self.returnType];
        }
        else {
            rslt = [response.result toJsonObjectFromDict:self.returnType];
        }
    }
    
    [self setResult:rslt];
}

-(void)setResult:(id)rslt{
    [waitLock lock];
    
    result = rslt;
    
    [waitLock broadcast];
    
    [waitLock unlock];
}

-(id)getResult{
    [waitLock lock];
    
    if(result == nil){
        [waitLock wait];
    }
    
    [waitLock unlock];
    
    return result;
}

@end
