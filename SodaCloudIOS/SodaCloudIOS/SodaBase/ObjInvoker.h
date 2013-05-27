//
//  ObjInvoker.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/21/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "InvocationMsg.h"
#import "InvocationResponseMsg.h"
#import "Soda.h"

@interface ObjInvoker : NSObject
{
    
}

@property(nonatomic,retain)Soda* soda;

-(id)initWithSoda:(Soda*)soda;
-(InvocationResponseMsg*)invoke:(InvocationMsg*)msg;
-(NSArray*)unmarshallParams:(NSArray*)params usingTypes:(NSArray*)types;
-(SodaMethod*)findMethod:(NSString*)methodName inObj:(id)target;

@end
