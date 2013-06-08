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
    SODA_METHOD_RETURN_TYPE_FROM_PARAM(@"get", 1, PARAM(NSString))
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

-(ObjRef*)refForObject:(id<NSObject>)obj
{
    // we have to do this b/c the passed obj
    // may not implement NSCopying and dict
    // keys must implement it
    NSNumber* hash = @([obj hash]);
    return [self.refs objectForKey:hash];
}

-(void)bindObject:(id)obj toRef:(ObjRef*)ref
{
    [self.bindings setObject:obj forKey:ref.uri];
    
    // we have to do this b/c the passed obj
    // may not implement NSCopying and dict
    // keys must implement it
    NSNumber* hash = @([obj hash]);
    [self.refs setObject:ref forKey:hash];
}

-(ObjRef*)bindObject:(id)obj toId:(NSString*)oid
{
    ObjRef* ref = [self refForObject:obj];
    
    if(ref == nil){
        ref = [[ObjRef alloc]init];
        [ref setHost:self.host andObjId:oid];
        [self.bindings setObject:obj forKey:oid];
        [self.refs setObject:ref forKey:oid];
        
        [self bindObject:obj toRef:ref];
    }
    
    return ref;
}

-(id)get:(NSString*)name asType:(Class)type
{
    id obj = [self.bindings objectForKey:name];
    if(obj == nil){
        obj = [self.parent get:name asType:type];
    }
    return obj;
}

-(id)getObject:(ObjRef*)ref
{
    id obj = [self.bindings objectForKey:ref.uri];
    return obj;
}

-(ObjRef*)bindObject:(id)obj
{
    return [self bindObject:obj toId:[[NSUUID UUID] UUIDString]];
}


@end
