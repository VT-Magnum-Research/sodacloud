//
//  SodaViewController.h
//  SodaCloudIOS
//
//  Created by Jules White on 5/16/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "SodaBase/SodaListener.h"

@interface SodaViewController : UIViewController<MDWampDelegate,SodaListener>


-(void) connected:(Soda*)soda;
-(void) disconnected;

@end
