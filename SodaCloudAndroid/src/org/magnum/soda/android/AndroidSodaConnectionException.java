/* 
 **
 ** Copyright 2013, Jules White
 **
 ** 
 */
package org.magnum.soda.android;

public class AndroidSodaConnectionException extends Exception {

	private int code_;
	private String reason_;

	public AndroidSodaConnectionException(int code, String reason) {
		super();
		code_ = code;
		reason_ = reason;
	}

	public AndroidSodaConnectionException(Throwable throwable) {
		super(throwable);
	}

	public int getCode() {
		return code_;
	}

	public void setCode(int code) {
		code_ = code;
	}

	public String getReason() {
		return reason_;
	}

	public void setReason(String reason) {
		reason_ = reason;
	}

}
