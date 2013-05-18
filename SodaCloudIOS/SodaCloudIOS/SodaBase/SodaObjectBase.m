//
//  SodaObjectBase.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/17/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "SodaObjectBase.h"

@implementation SodaObjectBase

-(id)init
{
    self = [super init];
    if(!self){
        return nil;
    }
    
    mappings = [[NSDictionary alloc]init];
    
    return self;
}


-(NSDictionary*)typeMappings
{
    return mappings;
}

@end
