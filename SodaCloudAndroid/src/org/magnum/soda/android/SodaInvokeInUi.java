/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.android;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation marks methods that
 * should always be invoked by Soda in
 * the context of the UI thread on 
 * Android. 
 * 
 * WARNING: If you apply this annotation,
 * you cannot invoke any blocking Soda
 * methods (e.g. any Soda methods that
 * are not void and annotated with 
 * @SodaAsync) inside of the method
 * or Android will freak out for doing
 * network ops in the gui thread.
 * 
 * @author jules
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SodaInvokeInUi {

}
