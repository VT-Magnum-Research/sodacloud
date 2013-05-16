//
//  InvocationMsg.h
//  SodaIOS
//
//  Created by Jules White on 5/16/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "Msg.h"

@interface InvocationMsg : Msg

@property(nonatomic,assign)NSString* method;
@property(nonatomic,assign)NSString* uri;
@property(nonatomic,assign)NSArray* parameters;

@end
