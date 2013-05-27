//
//  MockSvcImpl.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/27/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "MockSvcImpl.h"


@implementation MockSvcImpl 


-(NSString*)foo:(NSString*)val;
{
    return val;
}

-(void)doIt:(NSNumber*)a b:(NSString*)b c:(MockSvc*)test
{
    
}

-(NSDictionary*)doItAndReturnParams:(NSNumber*)a b:(NSString*)b c:(MockSvc*)test
{
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]init];
    [dict setObject:a forKey:@"a"];
    [dict setObject:b forKey:@"b"];
    [dict setObject:test forKey:@"test"];
    return dict;
}


-(MockSvc*)getTest
{
    return self;
}

-(void)setTest:(MockSvc*)test
{
    
}

@end
