//
//  SodaObject.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/17/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "SodaMethod.h"

@protocol SodaObject <NSObject>

#define SODA_METHODS(...) -(NSArray*)sodaMethods{\
return [NSArray arrayWithObjects:__VA_ARGS__, nil];}

@required
-(NSArray*)sodaMethods;
@end

