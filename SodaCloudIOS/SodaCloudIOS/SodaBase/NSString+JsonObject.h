//
//  NSString+JsonObject.h
//  SodaIOS
//
//  Created by Jules White on 5/15/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSString (JsonObject)
-(id) toJsonObject:(Class)type;
@end
