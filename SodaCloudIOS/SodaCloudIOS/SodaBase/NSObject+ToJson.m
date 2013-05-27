//
//  NSObject+ToJson.m
//  SodaIOS
//
//  Created by Jules White on 5/16/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "NSObject+ToJson.h"

#import "ObjRef.h"
#import "Soda.h"
#import "ObjProxy.h"
#import "SodaObject.h"
#import <objc/runtime.h>
#import "SodaPass.h"
#import <SBJson/SBJson.h>
#import <DCKeyValueObjectMapping/DCKeyValueObjectMapping.h>

@implementation NSObject (ToJson)

-(NSString*) toJson
{
    NSDictionary* attrs = [self dictionaryValueOfObject:self andSoda:nil];
    return [attrs JSONRepresentation];
}

-(NSString*) toJsonWithSoda:(Soda*)soda
{
    NSDictionary* attrs =
    [self dictionaryValueOfObject:self andSoda:soda];
    return [attrs JSONRepresentation];
}

-(NSDictionary *) dictionaryWithPropertiesOfObject:(id)obj
{
    
    return [self dictionaryWithPropertiesOfObject:obj asType:[obj class]];
}

-(NSDictionary *) dictionaryWithPropertiesOfObject:(id)obj asType:(Class)type 
{
    return [self dictionaryWithPropertiesOfObject:obj asType:type andSoda:nil];
}

-(NSDictionary *) dictionaryWithPropertiesOfObject:(id)obj asType:(Class)type andSoda:(Soda*)soda
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
            if(soda != nil &&
               [val conformsToProtocol:@protocol(SodaObject)] &&
               ![SodaPass isByReference:[val class]]
               ){
                val = [soda bindObject:val];
                val = [self dictionaryForObjRef:val];
            }
            else {
                val = [self dictionaryValueOf:val andSoda:soda];
            }
            
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

-(id) arrayValueOf:(NSArray*)arr andSoda:(Soda*)soda
{
    NSMutableArray* val = [[NSMutableArray alloc]initWithCapacity:arr.count];
    for (int i = 0; i < arr.count; i++) {
        id obj = [arr objectAtIndex:i];
        obj = [self dictionaryValueOf:obj andSoda:soda];
        [val addObject:obj];
    }
    
    return val;
}

-(id) dictionaryValueOf:(id)value andSoda:(Soda*)soda
{
    if ([value isKindOfClass:[NSNumber class]]){
         return value;
    }
    else if ([value isKindOfClass:[NSString class]]){
         return value;
    }
    else {
        return [self dictionaryValueOfObject:value andSoda:soda];
    }
}

-(id) dictionaryValueOfObject:(id)value andSoda:(Soda*)soda
{
    if([value isKindOfClass:[ObjRef class]]){
        return [self dictionaryForObjRef:value];
    }
    else if([value isKindOfClass:[ObjProxy class]]){
        ObjProxy* prox = value;
        ObjRef* ref = prox.target;
        return [self dictionaryForObjRef:ref];
    }
    else if([value isKindOfClass:[NSArray class]]){
        return [self arrayValueOf:value andSoda:soda];
    }
    else if([SodaPass isByReference:[value class]]){
        // convert to an ObjRef and return
        return [self dictionaryWithPropertiesOfObject:value asType:[value class] andSoda:nil];
    }
    else if(soda != nil &&
       [value conformsToProtocol:@protocol(SodaObject)]){
        value = [soda bindObject:value];
        return [self dictionaryForObjRef:value];
    }
    else {
        return [self dictionaryWithPropertiesOfObject:value asType:[value class] andSoda:soda];
    }
}

@end
