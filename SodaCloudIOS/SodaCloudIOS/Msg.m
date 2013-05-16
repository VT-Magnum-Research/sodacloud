//
//  Msg.m
//  SodaIOS
//
//  Created by Jules White on 5/15/13.
//  Copyright (c) 2013 Jules White. All rights reserved.
//

#import "Msg.h"

@implementation Msg    
//    - (NSDictionary *)dictionaryOfPropertiesForObject:(id)object
//    {
//        // somewhere to store the results
//        NSMutableDictionary *result = [NSMutableDictionary dictionary];
//        
//        // we'll grab properties for this class and every superclass
//        // other than NSObject
//        Class classOfObject = [object class];
//        while(![classOfObject isEqual:[NSObject class]])
//        {
//            // ask the runtime to give us a C array of the properties defined
//            // for this class (which doesn't include those for the superclass)
//            unsigned int numberOfProperties;
//            objc_property_t  *properties =
//            class_copyPropertyList(classOfObject, &numberOfProperties);
//            
//            // go through each property in turn...
//            for(
//                int propertyNumber = 0;
//                propertyNumber < numberOfProperties;
//                propertyNumber++)
//            {
//                // get the name and convert it to an NSString
//                NSString *nameOfProperty = [NSString stringWithUTF8String:
//                                            property_getName(properties[propertyNumber])];
//                
//                // use key-value coding to get the property value
//                id propertyValue = [object valueForKey:nameOfProperty];
//                
//                // add the value to the dictionary —
//                // we'll want to transmit NULLs, even though an NSDictionary
//                // can't store nils
//                [result
//                 setObject:propertyValue ? propertyValue : [NSNull null]
//                 forKey:nameOfProperty];
//            }
//            
//            // we took a copy of the property list, so...
//            free(properties);
//            
//            // we'll want to consider the superclass too
//            classOfObject = [classOfObject superclass];
//        }
//        
//        // return the dictionary
//        return result;
//    }

@end
