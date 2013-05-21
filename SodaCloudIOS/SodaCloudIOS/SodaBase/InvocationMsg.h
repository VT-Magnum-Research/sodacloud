//
//  InvocationMsg.h
//  SodaIOS
//
//  Created by Jules White on 5/16/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "Msg.h"

@interface InvocationMsg : Msg

@property(nonatomic,retain)NSString* method;
@property(nonatomic,retain)NSString* uri;
@property(nonatomic,retain)NSArray* parameters;

@end
