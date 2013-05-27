//
//  ObjProxy.h
//  SodaIOS
//
//  Created by Jules White on 5/15/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ObjRef.h"
#import "SodaObject.h"
#import "ResponseCatcherFactory.h"
#import "Soda.h"

#define PROXY(ref,type) [[ObjProxy alloc]initWithObjRef:ref andSodaObject:[[type alloc]init]];

@interface ObjProxy : NSProxy
{
    NSCondition* waitLock;
    id interface;
}

@property (nonatomic, retain) ObjRef* target;
@property (nonatomic, retain) id<ResponseCatcherFactory> catcherFactory;
@property (nonatomic, retain) Soda* soda;

- (id)initWithObjRef:(ObjRef*)ref andSodaObject:(id)spec;

@end
