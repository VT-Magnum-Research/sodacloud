//
//  SodaPass.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/19/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface SodaPass : NSObject
{}
+(void)byReference:(Class)type;
+(BOOL)isByReference:(Class)type;
@end
