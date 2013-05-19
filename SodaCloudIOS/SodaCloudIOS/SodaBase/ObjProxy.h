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

#define PROXY(ref,type) [[ObjProxy alloc]initWithObjRef:ref andSodaObject:[[type alloc]init]];

@interface ObjProxy : NSProxy
{
    ObjRef* target;
    NSCondition* waitLock;
    id interface;
}

@property (nonatomic, assign) ObjRef* remoteObj;
@property (nonatomic, assign) id<ResponseCatcherFactory> catcherFactory;

- (id)initWithObjRef:(ObjRef*)ref andSodaObject:(id)spec;

@end
