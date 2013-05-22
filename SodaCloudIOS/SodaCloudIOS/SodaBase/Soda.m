//
//  Soda.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/19/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "Soda.h"

#import "ObjProxy.h"
#import "ObjRef.h"

@implementation Soda

-(id)init
{
    self = [super init];
    if(!self){
        return nil;
    }
    
    NSString* hostid = [[NSUUID UUID] UUIDString];
    self.host = [NSString stringWithFormat:@"soda://%@",hostid];
    self.namingService = [[NamingSvc alloc]initWithHost:self.host];
    
    return self;
}

-(id)createProxyWithRef:(ObjRef*)ref andType:(Class)type
{
    id proxy = [self.namingService getObject:ref];
    
    if(proxy == nil){
        ObjProxy* prox = PROXY(ref,type);
        prox.soda = self;
        proxy = prox;
        [self.namingService bindObject:proxy toRef:ref];
    }
    
    return proxy;
}

-(void)connected
{
    NamingSvc* root = [self createProxyWithRef:ROOT_NAMING_SVC_REF andType:[NamingSvc class]];
    self.namingService.parent = root;
}

-(void)disconnected
{
    self.namingService.parent = nil;
}

@end
