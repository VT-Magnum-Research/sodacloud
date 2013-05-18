//
//  NSDictionary+JsonObjectFromDict.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/17/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "NSDictionary+JsonObjectFromDict.h"

#import <DCKeyValueObjectMapping.h>

@implementation NSDictionary (JsonObjectFromDict)

-(id) toJsonObjectFromDict:(Class)jtype
{
    DCKeyValueObjectMapping *parser = [DCKeyValueObjectMapping mapperForClass: jtype];
    
    return [parser parseDictionary:self];
}
@end
