//
//  SodaMethod.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/18/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "SodaMethod.h"

@implementation SodaMethod

-(id)initWithName:(NSString*)name andReturnType:(Class)rtype, ...
{
    self = [super init];
    if(!self){
        return nil;
    }
    
    self.name = name;
    self.returnType = rtype;
    self.parameterTypes = [[NSMutableArray alloc]init];
    
    va_list args;
    va_start(args, rtype);
    Class value;
    
    while( (value = va_arg( args, Class)) ){
        [self.parameterTypes addObject:value];
    }
    va_end(args);
    
    return self;
}

-(id)initWithName:(NSString*)name, ...
{
    self = [super init];
    if(!self){
        return nil;
    }
    
    self.name = name;
    self.returnType = nil;
    self.parameterTypes = [[NSMutableArray alloc]init];
    
    va_list args;
    va_start(args, name);
    Class value;
    
    while( (value = va_arg( args, Class)) ){
        [self.parameterTypes addObject:value];
    }
    va_end(args);
    
    return self;
}


@end
