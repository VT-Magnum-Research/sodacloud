//
//  InvocationResponseMsg.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/21/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "Msg.h"

@interface InvocationResponseMsg : Msg
{
}

@property(nonatomic,retain)id result;
@property(nonatomic,retain)NSString* exception;

@end
