//
//  SodaPass.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/19/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "SodaPass.h"

@implementation SodaPass

static NSMutableDictionary* dict;

+(void)byReference:(Class)type
{
    NSMutableDictionary* refs = [SodaPass getRefs];
    [refs setObject:@(TRUE) forKey:(id <NSCopying>)type];
}

+(BOOL)isByReference:(Class)type
{
    BOOL byref = ([[SodaPass getRefs] objectForKey:type] != nil)? TRUE : FALSE;
    return byref;
}

+(NSMutableDictionary*)getRefs
{
    if(dict == nil){
        dict = [[NSMutableDictionary alloc]init];
    }
    
    return dict;
}
@end
