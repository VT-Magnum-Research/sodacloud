//
//  SodaObjectDecorator.m
//  SodaCloudIOS
//
//  Created by Jules White on 6/8/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "SodaObjectDecorator.h"
#import "SodaObject.h"

@implementation SodaObjectDecorator
{
    id<SodaObject> _interface;
    id _target;
}

-(id) getTarget
{
    return _target;
}

- (id)initWithTarget:(id) target andType:(id<SodaObject>)type
{
    self = [super init];
    
    if (self == nil)
    {
        return nil;
    }
    
    _interface = type;
    _target = target;
    
    return self;
}



- (id)forwardingTargetForSelector:(SEL)se
{
    if ([_target respondsToSelector:se]) {
        return _target;
    }
    else if([_interface respondsToSelector:se]){
        return _interface;
    }
    
    return self;
}

- (BOOL)respondsToSelector:(SEL)se
{
    return [super respondsToSelector:se] || [_interface respondsToSelector:se] || [_target respondsToSelector:se];
}

- (BOOL)isKindOfClass:(Class)aClass
{
    
    return [super isKindOfClass:aClass] || [_interface isKindOfClass:aClass] || [_target isKindOfClass:aClass];
}

-(NSMethodSignature*) methodSignatureForSelector:(SEL)sel
{
    if ([_target respondsToSelector:sel]) {
        return [_target methodSignatureForSelector:sel];
    }
    
    return [super methodSignatureForSelector:sel];
}

@end
