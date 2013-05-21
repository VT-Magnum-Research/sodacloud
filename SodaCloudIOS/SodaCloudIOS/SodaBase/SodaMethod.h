//
//  SodaMethod.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/18/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "RefParam.h"

#define PARAM(type) [type class]
#define REF(type) [[RefParam alloc]initWithType:[type class]]
#define SODA_METHOD(name,type, ...) [[SodaMethod alloc] initWithName:name andReturnType:[type class], __VA_ARGS__, nil]
#define SODA_NOARG_METHOD(name,type) [[SodaMethod alloc] initWithName:name andReturnType:[type class], nil]
#define SODA_VOID_METHOD(name, ...) [[SodaMethod alloc] initWithName:name, __VA_ARGS__, nil]
#define SODA_VOID_NOARG_METHOD(name, ...) [[SodaMethod alloc] initWithName:name, nil]



@interface SodaMethod : NSObject
{
    
}

-(id)initWithName:(NSString*)name andReturnType:(Class)rtype, ...;
-(id)initWithName:(NSString*)name, ...;
-(NSString*)cTypes;
-(BOOL)isAsyncIfVoid;

@property(nonatomic,retain)NSString* name;
@property(nonatomic)Class returnType;
@property(nonatomic)NSMutableArray* parameterTypes;

@end
