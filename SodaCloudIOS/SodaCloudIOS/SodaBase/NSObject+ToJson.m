//
//  NSObject+ToJson.m
//  SodaIOS
//
//  Created by Jules White on 5/16/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "NSObject+ToJson.h"

#import "ObjRef.h"
#import <objc/runtime.h>
#import "SodaPass.h"
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
    
    return [self dictionaryWithPropertiesOfObject:obj asType:[obj class]];
}

-(NSDictionary *) dictionaryWithPropertiesOfObject:(id)obj asType:(Class)type;
{
    
    if([obj isKindOfClass:[ObjRef class]]){
        return [self dictionaryForObjRef:obj];
    }
    
    NSMutableDictionary *dict = [NSMutableDictionary dictionary];
    
    unsigned count;
    objc_property_t *properties = class_copyPropertyList(type, &count);
    
    for (int i = 0; i < count; i++) {
        NSString *key = [NSString stringWithUTF8String:property_getName(properties[i])];
        id val = [obj valueForKey:key];
        
        if(val != nil){
            val = [self dictionaryValueOf:val];
            [dict setObject:val forKey:key];
        }
    }
    
    free(properties);
    
    Class supc = class_getSuperclass(type);
    if(supc != nil && supc != [NSObject class] && supc != [NSProxy class]){
        [dict addEntriesFromDictionary:[self dictionaryWithPropertiesOfObject:obj asType:supc]];
    }
    
    return [NSDictionary dictionaryWithDictionary:dict];
}

-(NSDictionary*)dictionaryForObjRef:(ObjRef*)oref
{
        NSMutableDictionary* dict = [[NSMutableDictionary alloc]init];
        [dict setObject:@"ObjRef" forKey:@"type"];
        [dict setObject:oref.uri forKey:@"uri"];
        [dict setObject:[oref getHost] forKey:@"host"];
        return dict;
}

-(id) arrayValueOf:(NSArray*)arr
{
    NSMutableArray* val = [[NSMutableArray alloc]initWithCapacity:arr.count];
    for (int i = 0; i < arr.count; i++) {
        id obj = [arr objectAtIndex:i];
        obj = [self dictionaryValueOf:obj];
        [val addObject:obj];
    }
    
    return val;
}

-(id) dictionaryValueOf:(id)value
{
    if ([value isKindOfClass:[NSNumber class]]){
         return value;
    }
    else if ([value isKindOfClass:[NSString class]]){
         return value;
    }
    else if([value isKindOfClass:[ObjRef class]]){
        return [self dictionaryForObjRef:value];
    }
    else if([value isKindOfClass:[NSArray class]]){
        return [self arrayValueOf:value];
    }
    else if([SodaPass isByReference:[value class]]){
        // convert to an ObjRef and return
        // return [self dictionaryForObjRef:ref];
    }
    else {
        return [self dictionaryWithPropertiesOfObject:value];
    }
}

@end
