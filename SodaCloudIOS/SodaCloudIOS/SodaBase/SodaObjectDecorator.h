//
//  SodaObjectDecorator.h
//  SodaCloudIOS
//
//  Created by Jules White on 6/8/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "SodaObject.h"

@interface SodaObjectDecorator : NSObject
- (id)initWithTarget:(id) target andType:(id<SodaObject>)type;
- (id)getTarget;
@end
