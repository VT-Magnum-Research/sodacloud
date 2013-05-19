//
//  SodaMethod.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/18/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "SodaMethod.h"

@implementation SodaMethod

-(BOOL)isAsyncIfVoid
{
    return TRUE;
}

-(NSString*)cTypes
{
    NSMutableString* ctypes = [[NSMutableString alloc]init];
    NSString* rctype = (self.returnType == nil)? @"v" : @"@";
    [ctypes appendFormat:@"%@^v^c",rctype];
    
    for(Class c in self.parameterTypes){
        NSString* ctype = [self cTypeOf:c];
        [ctypes appendFormat:@"%@",ctype];
    }
    return ctypes;
}

-(NSString*)cTypeOf:(Class)type
{
    return @"@";
    
//    switch (argType[0])
//        {
//            case '#':
//            case '@':
//            {
//                id value;
//                [self getArgument:&value atIndex:argIndex];
//                return value;
//            }
//            case ':':
//            {
//                SEL s = (SEL)0;
//                [self getArgument:&s atIndex:argIndex];
//                id value = NSStringFromSelector(s);
//                return value;
//            }
//            case 'i':
//            {
//                int value;
//                [self getArgument:&value atIndex:argIndex];
//                return [NSNumber numberWithInt:value];
//            }
//            case 's':
//            {
//                short value;
//                [self getArgument:&value atIndex:argIndex];
//                return [NSNumber numberWithShort:value];
//            }
//            case 'l':
//            {
//                long value;
//                [self getArgument:&value atIndex:argIndex];
//                return [NSNumber numberWithLong:value];
//            }
//            case 'q':
//            {
//                long long value;
//                [self getArgument:&value atIndex:argIndex];
//                return [NSNumber numberWithLongLong:value];
//            }
//            case 'c':
//            {
//                char value;
//                [self getArgument:&value atIndex:argIndex];
//                return [NSNumber numberWithChar:value];
//            }
//            case 'C':
//            {
//                unsigned char value;
//                [self getArgument:&value atIndex:argIndex];
//                return [NSNumber numberWithUnsignedChar:value];
//            }
//            case 'I':
//            {
//                unsigned int value;
//                [self getArgument:&value atIndex:argIndex];
//                return [NSNumber numberWithUnsignedInt:value];
//            }
//            case 'S':
//            {
//                unsigned short value;
//                [self getArgument:&value atIndex:argIndex];
//                return [NSNumber numberWithUnsignedShort:value];
//            }
//            case 'L':
//            {
//                unsigned long value;
//                [self getArgument:&value atIndex:argIndex];
//                return [NSNumber numberWithUnsignedLong:value];
//            }
//            case 'Q':
//            {
//                unsigned long long value;
//                [self getArgument:&value atIndex:argIndex];
//                return [NSNumber numberWithUnsignedLongLong:value];
//            }
//            case 'f':
//            {
//                float value;
//                [self getArgument:&value atIndex:argIndex];
//                return [NSNumber numberWithFloat:value];
//            }
//            case 'd':
//            {
//                double value;
//                [self getArgument:&value atIndex:argIndex];
//                return [NSNumber numberWithDouble:value];
//            }
//            case 'B':
//            {
//                bool value;
//                [self getArgument:&value atIndex:argIndex];
//                return [NSNumber numberWithBool:value];
//            }
//            case '^':
//            {
//                void *value = NULL;
//                [self getArgument:&value atIndex:argIndex];
//                return [NSValue valueWithPointer:value];
//            }
//            case '{': // structure
//            {
//                NSUInteger maxArgSize = [[self methodSignature] frameLength];
//                NSMutableData *argumentData = [[[NSMutableData alloc] initWithLength:maxArgSize] autorelease];
//                [self getArgument:[argumentData mutableBytes] atIndex:argIndex];
//                return [NSValue valueWithBytes:[argumentData bytes] objCType:argType];
//            }
//                
//        }
}

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
