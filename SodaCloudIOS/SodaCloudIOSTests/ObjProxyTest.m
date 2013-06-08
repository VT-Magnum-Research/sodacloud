//
//  ObjProxyTest.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/18/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "ObjProxyTest.h"

#import "ObjProxy.h"
#import "SodaObject.h"
#import "MockCatcherFactory.h"

@interface MockSodaSvc : NSObject<SodaObject>
-(NSString*)foo:(NSString*)val;
-(void)doIt:(NSNumber*)a b:(NSString*)b c:(ObjProxyTest*)test;
-(ObjProxyTest*)getTest;
-(void)setTest:(ObjProxyTest*)test;
-(id)get:(NSString*)oid asType:(Class)type;
@end
@implementation MockSodaSvc

SODA_METHODS(
             SODA_METHOD(@"foo", NSString, PARAM(NSString)),
             SODA_VOID_METHOD(@"doIt", PARAM(NSNumber),PARAM(NSString),PARAM(ObjProxyTest)),
             SODA_NOARG_METHOD(@"getTest", ObjProxyTest),
             SODA_VOID_METHOD(@"setTest",REF(ObjProxyTest)),
             SODA_METHOD_RETURN_TYPE_FROM_PARAM(@"get", 1, PARAM(NSString))
            )
@end


@implementation ObjProxyTest

-(void)testInvocation
{
    ObjRef* ref = [[ObjRef alloc]init];
    [ref setUri:@"soda://localhost#foo"];
    
    //make sure that these construction methods work
    id proxy = PROXY(ref,MockSodaSvc);
    STAssertTrue(proxy != nil, @"The proxy construction macro PROXY failed.");
    id proxy2 = [ref toProxy:[MockSodaSvc class]];
    STAssertTrue(proxy2 != nil, @"The proxy construction method on ObjRef failed.");

    
    ObjProxy* proxy3 = [[ObjProxy alloc]initWithObjRef:ref andSodaObject:[[MockSodaSvc alloc] init]];
    MockCatcherFactory* fact = [[MockCatcherFactory alloc]init];
    [fact setResult:@"correct_result"];
    proxy3.catcherFactory = fact;
    id svc = proxy3;
    
    id rslt = [svc foo:@"asdf"];
    
    STAssertEquals(rslt, @"correct_result", @"The proxy method invocation returned the wrong result.");

    proxy3.catcherFactory = nil;
    int v = 1;
    [svc doIt:@(v) b:@"a" c:self];
    
    proxy3.catcherFactory = fact;
    [fact setResult:self];
    
    rslt = [svc getTest];
    
    STAssertEquals(rslt, self, @"The proxy method invocation returned the wrong result.");

    [fact setResult:self];
    rslt = [svc get:@"foo" asType:[self class]];
    STAssertEquals(rslt, self, @"The proxy method invocation returned the wrong result.");
    //id proxy2 = [ref toProxy:[MockSodaSvc class]];
}

@end
