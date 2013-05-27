//
//  NSArray+JsonObjects.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/17/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Soda.h"

@interface NSArray (JsonObjects)

-(NSArray*)toJsonObjectsOfTypes:(NSArray*)types;
-(NSArray*)toJsonObjectsOfTypes:(NSArray*)types andSoda:(Soda*)soda;
@end
