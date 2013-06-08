//
//  NamingSvcTest.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/20/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "NamingSvcTest.h"

#import "NamingSvc.h"
#import "MockSvc.h"
#import "MockSvcImpl.h"
#import <objc/runtime.h>

@implementation NamingSvcTest


// If this method blows up, it is probably because the
// MockSvc does not respond to the copyWithZone selector,
// even though it should.
-(void)testCopyWithZoneBug
{
    MockSvc* svc = [[MockSvcImpl alloc]init];
    NamingSvc* naming = [[NamingSvc alloc]initWithHost:@"test"];
    ObjRef* ref = [naming bindObject:svc toId:@"svc"];
    
    svc = [naming get:@"svc" asType:[MockSvc class]];
    STAssertNotNil(svc, @"The mock service was not stored properly by the naming service.");
    
    svc = [naming getObject:ref];
    STAssertNotNil(svc, @"The mock service was not stored properly by the naming service.");
    
}

-(void)testInvocation
{
    
    NSString* obj = @"a";
    NSString* obj2 = @"b";
    NSString* obj3 = @"c";
    
    NamingSvc* svc = [[NamingSvc alloc]initWithHost:@"test"];
    ObjRef* ref = [svc bindObject:obj];
    ObjRef* ref2 = [svc bindObject:obj2 toId:@"b"];
    ObjRef* ref3 = [svc bindObject:obj3];
    
    STAssertEqualObjects(ref,[svc bindObject:obj], @"The naming svc did not reuse the existing obj ref.");
    STAssertEqualObjects(ref2,[svc bindObject:obj2], @"The naming svc did not reuse the existing obj ref.");
    STAssertEqualObjects(ref3,[svc bindObject:obj3], @"The naming svc did not reuse the existing obj ref.");
    STAssertEqualObjects(ref2.uri,@"soda://test#b", @"The naming svc did create a correctly named obj ref.");
    
    STAssertEqualObjects([svc getObject:ref],obj, @"The naming svc did not return the correct object for the ref.");
    STAssertEqualObjects([svc getObject:ref2],obj2, @"The naming svc did not return the correct object for the ref.");
    STAssertEqualObjects([svc getObject:ref3],obj3, @"The naming svc did not return the correct object for the ref.");
    
    id obj4 = @"from_parent";
    NamingSvc* parent = [[NamingSvc alloc]initWithHost:@"parent"];
    [parent bindObject:obj4 toId:@"with_parent"];
    
    svc.parent = parent;
    STAssertEqualObjects([svc get:@"with_parent" asType:[NSString class]],obj4, @"The naming svc did not return the correct object from the parent naming svc.");
    
}

@end
