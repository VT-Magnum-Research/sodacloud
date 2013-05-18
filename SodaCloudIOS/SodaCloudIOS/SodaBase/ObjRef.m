//
//  ObjRef.m
//  SodaIOS
//
//  Created by Jules White on 5/15/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "ObjRef.h"

@implementation ObjRef


-(void)setHost:(NSString*)host andObjId:(NSString*)oid
{
    self.uri = [NSString stringWithFormat:@"soda://%@#%@",host,oid];
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
