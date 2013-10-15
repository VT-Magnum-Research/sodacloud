//
//  SodaViewController.m
//  SodaCloudIOS
//
//  Created by Jules White on 5/16/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <DCKeyValueObjectMapping/DCKeyValueObjectMapping.h>
#import <SBJson/SBJson.h>
#import "SodaBase/NSObject+ToJson.h"
#import "SodaBase/NSString+JsonObject.h"
#import "MDWamp.h"
#import "Msg.h"

#import "SodaViewController.h"


@interface MaintenanceReport : NSObject

@property(nonatomic,retain)NSString* id;
@property(nonatomic,retain)NSString* contents;
@end
@implementation MaintenanceReport
@end

@interface MaintenanceListener : NSObject<SodaObject>
@end
@implementation MaintenanceListener
    SODA_METHODS(
        SODA_VOID_METHOD(@"reportAdded", PARAM(MaintenanceReport))
    )
@end

@interface MaintenanceReports : NSObject<SodaObject>
-(void)addListener:(MaintenanceListener*)listener;
@end
@implementation MaintenanceReports
    SODA_METHODS(
        SODA_VOID_METHOD(@"addListener",REF(MaintenanceListener))
    )
@end




@interface SodaViewController ()

@end

@implementation SodaViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
     NSString* host = @"ws://localhost:8081";
    //
    Soda* soda = [[Soda alloc]init];
    [soda connect:host withListener:self];
}

-(void) reportAdded:(MaintenanceReport*)report
{
    NSLog(@"Added report: id:%@ content:%@",report.id, report.contents);
}

-(void) connected:(Soda*)soda
{
    id obj = [soda.namingService get:@"maintenance" asType:[MaintenanceReports class]];
    NSLog(@"From namingsvc: %@",obj);
    
    [obj addListener:self];
}

-(void) disconnected
{
    
}


- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
