//
//  PingSvc.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/17/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "SodaObject.h"

@protocol PingSvc <NSObject,SodaObject>

@required
-(void)ping;


@end
