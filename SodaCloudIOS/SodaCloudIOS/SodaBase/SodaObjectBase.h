//
//  SodaObjectBase.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/17/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "SodaObject.h"

@interface SodaObjectBase : NSObject<SodaObject>
{
    NSDictionary* mappings;
}
-(NSDictionary*)typeMappings;

@end
