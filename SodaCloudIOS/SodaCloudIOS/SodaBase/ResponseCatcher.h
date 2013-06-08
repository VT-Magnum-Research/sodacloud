//
//  ResponseCatcher.h
//  SodaIOS
//
//  Created by Jules White on 5/15/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "InvocationResponseMsg.h"
#import "Soda.h"

@interface ResponseCatcher : NSObject
{
    NSCondition* waitLock;
    id result;
}

@property(nonatomic,retain)Class returnType;
@property(nonatomic,retain)Soda* soda;
@property(nonatomic,retain)NSString* msgId;

-(void)setResultFromResponse:(InvocationResponseMsg*)response;
-(void)setResult:(id)rslt;
-(id)getResult;
-(id)initWithId:(NSString*)id andReturnType:(Class)type;
-(id)initWithId:(NSString*)id andReturnType:(Class)type andSoda:(Soda*)soda;

@end
