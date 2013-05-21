//
//  RefParam.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/19/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "RefParam.h"

@implementation RefParam
-(id)initWithType:(Class)type
{
    self = [super init];
    if(!self){
        return nil;
    }
    self.type = type;
    return self;
}
@end