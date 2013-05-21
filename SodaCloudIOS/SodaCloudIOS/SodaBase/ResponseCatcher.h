//
//  ResponseCatcher.h
//  SodaIOS
//
//  Created by Jules White on 5/15/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ResponseCatcher : NSObject
{
    NSCondition* waitLock;
    id result;
}

@property(nonatomic,retain)Class returnType;

-(void)setResult:(id)rslt;
-(id)getResult;
-(id)initWithId:(NSString*)id andReturnType:(Class)type;
@end
