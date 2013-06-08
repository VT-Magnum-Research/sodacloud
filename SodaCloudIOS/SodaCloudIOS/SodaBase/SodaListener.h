//
//  SodaListener.h
//  SodaCloudIOS
//
//  Created by Jules White on 6/7/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Soda.h"

@class Soda;

@protocol SodaListener <NSObject>

-(void) connected:(Soda*)soda;
-(void) disconnected;

@end
