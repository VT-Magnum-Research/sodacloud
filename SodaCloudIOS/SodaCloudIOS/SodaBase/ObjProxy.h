//
//  ObjProxy.h
//  SodaIOS
//
//  Created by Jules White on 5/15/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ObjRef.h"

@interface ObjProxy : NSProxy
{
    NSCondition* waitLock;
}

@property (nonatomic, assign) ObjRef* remoteObj;

- (id)initWithObjRef:(ObjRef*)ref;

@end
