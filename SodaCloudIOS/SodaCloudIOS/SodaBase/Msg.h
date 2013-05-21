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

@property(nonatomic,retain)NSString* id;
@property(nonatomic,retain)NSString* responseTo;
@property(nonatomic,retain)NSString* destination;
@property(nonatomic,retain)NSString* type;
@property(nonatomic,retain)NSString* source;
@end
