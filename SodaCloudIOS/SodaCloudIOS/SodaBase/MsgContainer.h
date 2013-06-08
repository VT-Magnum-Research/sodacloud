//
//  MsgContainer.h
//  SodaCloudIOS
//
//  Created by Jules White on 6/6/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface MsgContainer : NSObject
{
    
}

-(NSDictionary*)toDict;

@property(nonatomic,retain) NSString* msg;
@property(nonatomic,retain) NSString* destination;

@end
