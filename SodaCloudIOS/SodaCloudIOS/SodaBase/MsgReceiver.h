//
//  MsgReceiver.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/28/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol MsgReceiver <NSObject>

-(void) received:(Msg*)msg;

@end
