//
//  PingSvc.h
//  SodaIOS
//
//  Created by Jules White on 5/14/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol PingSvc <NSObject>

+(void) ping;
+(void) pingMe:(PingSvc)withMe;
@end
