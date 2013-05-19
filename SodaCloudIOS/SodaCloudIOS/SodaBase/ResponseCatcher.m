//
//  ResponseCatcher.m
//  SodaIOS
//
//  Created by Jules White on 5/15/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "ResponseCatcher.h"

@implementation ResponseCatcher

-(id)initWithId:(NSString *)id{
    waitLock = [[NSCondition alloc] init];
    return self;
}

-(void)setResult:(id)rslt{
    [waitLock lock];
    
    result = rslt;
    
    [waitLock broadcast];
    
    [waitLock unlock];
}

-(id)getResult{
    [waitLock lock];
    
    if(result == nil){
        [waitLock wait];
    }
    
    [waitLock unlock];
    
    return result;
}

@end
