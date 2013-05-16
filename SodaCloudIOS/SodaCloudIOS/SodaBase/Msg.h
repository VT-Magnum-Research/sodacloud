//
//  Msg.h
//  SodaIOS
//
//  Created by Jules White on 5/15/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Msg : NSObject
{
    
}

@property(nonatomic,assign)NSString* id;
@property(nonatomic,assign)NSString* responseTo;
@property(nonatomic,assign)NSString* destination;
@property(nonatomic,assign)NSString* type;
@end
