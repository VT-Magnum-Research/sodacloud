//
//  ObjRef.m
//  SodaIOS
//
//  Created by Jules White on 5/15/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "ObjRef.h"
#import "ObjProxy.h"

@implementation ObjRef

-(id)initWithUri:(NSString*)uri
{
    self = [super init];
    if(self == nil){
        return nil;
    }
    
    self.uri = uri;
    
    return self;
}

-(id)toProxy:(Class)type
{
    return [[ObjProxy alloc]initWithObjRef:self andSodaObject:[[type alloc]init]];
}

-(void)setHost:(NSString*)host andObjId:(NSString*)oid
{
    self.uri = ([host rangeOfString:@"soda://"].location != NSNotFound)?
          [NSString stringWithFormat:@"%@#%@",host,oid]
        : [NSString stringWithFormat:@"soda://%@#%@",host,oid];
}

-(NSString*)getHost
{
    NSString* host = [[self.uri componentsSeparatedByString:@"#"] objectAtIndex:0];
    return host;
}

- (NSUInteger)hash
{
    return [self.uri hash];
}

- (BOOL)isEqual:(id)other
{
    if (other == self)
        return YES;
    if (!other || ![other isKindOfClass:[self class]])
        return NO;
    else {
        ObjRef* ref = other;
        return [self.uri isEqualToString:ref.uri];
    }
}

@end
