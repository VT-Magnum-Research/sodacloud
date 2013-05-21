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

-(id)initWithUri:(NSString*)uri;
-(void)setHost:(NSString*)host andObjId:(NSString*)oid;
-(NSString*)getHost;
-(id)toProxy:(Class)type;

@property (nonatomic, assign) NSString* uri;

@end
