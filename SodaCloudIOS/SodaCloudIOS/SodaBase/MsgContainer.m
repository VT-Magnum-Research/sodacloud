//
//  MsgContainer.m
//  SodaCloudIOS
//
//  Created by Jules White on 6/6/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "MsgContainer.h"

@implementation MsgContainer

-(NSDictionary*)toDict
{
    NSMutableDictionary* dict = [[NSMutableDictionary alloc]init];
    [dict setObject:self.msg forKey:@"msg"];
    [dict setObject:self.destination forKey:@"destination"];
    return dict;
}

@end
