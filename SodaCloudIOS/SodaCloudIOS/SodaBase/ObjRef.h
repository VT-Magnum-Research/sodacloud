//
//  ObjRef.h
//  SodaIOS
//
//  Created by Jules White on 5/15/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ObjRef : NSObject
{
    
}

-(void)setHost:(NSString*)host andObjId:(NSString*)oid;
-(NSString*)getHost;

@property (nonatomic, assign) NSString* uri;

@end
