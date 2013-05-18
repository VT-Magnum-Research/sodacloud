//
//  NSObject+ToJson.h
//  SodaIOS
//
//  Created by Jules White on 5/16/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSObject (ToJson)
-(NSString*) toJson;
-(NSDictionary *) dictionaryWithPropertiesOfObject:(id)obj;
-(id) dictionaryValueOf:(id)value;
@end
