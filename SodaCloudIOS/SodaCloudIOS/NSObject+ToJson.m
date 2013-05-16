//
//  NSObject+ToJson.m
//  SodaIOS
//
//  Created by Jules White on 5/16/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "NSObject+ToJson.h"

#import <objc/runtime.h>
#import <SBJson/SBJson.h>
#import <DCKeyValueObjectMapping/DCKeyValueObjectMapping.h>

@implementation NSObject (ToJson)

-(NSString*) toJson
{
    NSDictionary* attrs = [self dictionaryWithPropertiesOfObject:self];
    return [attrs JSONRepresentation];
}

-(NSDictionary *) dictionaryWithPropertiesOfObject:(id)obj
{
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    
    unsigned count;
    objc_property_t *properties = class_copyPropertyList([obj class], &count);
    
    for (int i = 0; i < count; i++) {
        NSString *key = [NSString stringWithUTF8String:property_getName(properties[i])];
        id val = [obj valueForKey:key];
        
        if(val != nil){
            [dict setObject:val forKey:key];
        }
    }
    
    free(properties);
    
    return [NSDictionary dictionaryWithDictionary:dict];
}

-(id) dictionaryValueOf:(id)value
{
    if ([value isKindOfClass:[NSNumber class]]){
         return value;
    }
    else if ([value isKindOfClass:[NSString class]]){
         return value;
    }
    else {
        return [self dictionaryWithPropertiesOfObject:value];
    }
}

@end
