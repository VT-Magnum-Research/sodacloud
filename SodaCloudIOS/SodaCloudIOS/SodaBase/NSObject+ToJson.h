//
//  NSObject+ToJson.h
//  SodaIOS
//
//  Created by Jules White on 5/16/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "Soda.h"

@interface NSObject (ToJson)
-(NSString*) toJson;
-(NSString*) toJsonWithSoda:(Soda*)soda;
-(NSDictionary *) dictionaryWithPropertiesOfObject:(id)obj;
-(NSDictionary *) dictionaryWithPropertiesOfObject:(id)obj asType:(Class)type andSoda:(Soda*)soda;
-(id) dictionaryValueOf:(id)value;
@end
