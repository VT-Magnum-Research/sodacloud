//
//  RefParam.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/19/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface RefParam : NSObject
{
}

@property(nonatomic,assign)Class type;
-(id)initWithType:(Class)type;

@end
