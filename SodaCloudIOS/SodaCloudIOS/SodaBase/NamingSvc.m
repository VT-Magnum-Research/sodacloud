//
//  NamingSvc.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/20/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "NamingSvc.h"

@implementation NamingSvc

SODA_METHODS(
    SODA_METHOD(@"get", NSObject, PARAM(NSString))
)

-(id)initWithHost:(NSString*)host
{
    self = [super init];
    if(!self){
        return nil;
    }
    
    self.host = host;
    self.bindings = [[NSMutableDictionary alloc]init];
    self.refs = [[NSMutableDictionary alloc]init];
    
    return self;
}

-(ObjRef*)bindObject:(id)obj toId:(NSString*)oid
{
    ObjRef* ref = [self.refs objectForKey:obj];
    
    if(ref == nil){
        ref = [[ObjRef alloc]init];
        [ref setHost:self.host andObjId:oid];
        [self.bindings setObject:obj forKey:oid];
        [self.bindings setObject:obj forKey:ref.uri];
        [self.refs setObject:ref forKey:obj];
        [self.refs setObject:ref forKey:oid];
    }
    
    return ref;
}

-(id)getObject:(ObjRef*)ref
{
    id obj = [self.bindings objectForKey:ref.uri];
    return obj;
}

-(id)get:(NSString*)name
{
    id obj = [self.bindings objectForKey:name];
    if(obj == nil){
        obj = [self.parent get:name];
    }
    return obj;
}

-(ObjRef*)bindObject:(id)obj
{
    return [self bindObject:obj toId:[[NSUUID UUID] UUIDString]];
}


@end
