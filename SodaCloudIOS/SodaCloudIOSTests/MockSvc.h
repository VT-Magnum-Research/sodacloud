//
//  MockSodaSvc.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/21/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "SodaObject.h"

@interface MockSvc : NSObject<SodaObject>
-(NSString*)foo:(NSString*)val;
-(void)doIt:(NSNumber*)a b:(NSString*)b c:(MockSvc*)test;
-(NSDictionary*)doItAndReturnParams:(NSNumber*)a b:(NSString*)b c:(MockSvc*)test;
-(MockSvc*)getTest;
-(void)setTest:(MockSvc*)test;
@end
