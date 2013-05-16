//
//  NSString+JsonObject.m
//  SodaIOS
//
//  Created by Jules White on 5/15/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//
#import <SBJson/SBJson.h>
#import <DCKeyValueObjectMapping/DCKeyValueObjectMapping.h>

#import "NSString+JsonObject.h"

@implementation NSString (JsonObject)

-(id) toJsonObject:(Class)jtype
{
    NSDictionary *jsonParsed = [self JSONValue];
    
    DCKeyValueObjectMapping *parser = [DCKeyValueObjectMapping mapperForClass: jtype];
    
    return [parser parseDictionary:jsonParsed];
}



@end
