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

-(void)setResult:(id)rslt;
-(id)getResult;
-(id)initWithId:(NSString*)id;
@end
