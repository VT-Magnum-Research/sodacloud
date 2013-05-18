//
//  NSArray+JsonObjects.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/17/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "NSArray+JsonObjects.h"

#import "NSDictionary+JsonObjectFromDict.h"

@implementation NSArray (JsonObjects)

-(NSArray*)toJsonObjectsOfTypes:(NSArray*)types
{
    NSMutableArray* objs = [[NSMutableArray alloc]initWithCapacity:types.count];
    
    for (int i = 0; i < types.count; i++) {
        Class c = [types objectAtIndex:i];
        id val = [self objectAtIndex:i];
        id obj = [self jsonObjectValueOf:val withType:c];
        [objs addObject:obj];
    }
    
    return objs;
}

-(id) jsonObjectValueOf:(id)value withType:(Class)type
{
    if ([value isKindOfClass:[NSNumber class]]){
        return value;
    }
    else if ([value isKindOfClass:[NSString class]]){
        return value;
    }
    else if([value isKindOfClass:[NSArray class]]){
        //nothing we can do here since we don't know
        //what types are supposed to be in the array
        return value;
    }
    else if([value isKindOfClass:[NSDictionary class]]){
        return [value toJsonObjectFromDict:type];
    }
    else {
        //no clue, bail out
        return value;
    }
}

@end
