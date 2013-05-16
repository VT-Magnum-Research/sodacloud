//
//  ObjProxy.m
//  SodaIOS
//
//  Created by Jules White on 5/15/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "ObjProxy.h"
#import "ResponseCatcher.h"

@implementation ObjProxy


- (id)initWithObjRef:(ObjRef*)ref
{
    waitLock = [[NSCondition alloc] init];
    return self;
}

- (void)forwardInvocation:(NSInvocation *)anInvocation
{
    ResponseCatcher* catcher = [[ResponseCatcher alloc] initWithId:@"asdf"];
    
    [anInvocation setReturnValue:[catcher getResult]];
}


@end
