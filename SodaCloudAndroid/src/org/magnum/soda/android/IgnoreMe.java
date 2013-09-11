/* 
**
** Copyright 2013, Jules White
**
** 
*/
package org.magnum.soda.android;

public class IgnoreMe {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AndroidSoda.init(null, "", 0, new AndroidSodaListener() {
			
			@Override
			public void connectionFailure(AndroidSoda s,
					AndroidSodaConnectionException ex) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void connected(AndroidSoda s) {
				// TODO Auto-generated method stub
				
			}
		});

	}

}
