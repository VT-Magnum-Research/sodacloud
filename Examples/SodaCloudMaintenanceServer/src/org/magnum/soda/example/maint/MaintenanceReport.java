/* 
 **
 ** Copyright 2013, Jules White
 **
 ** 
 */
package org.magnum.soda.example.maint;

import org.magnum.soda.proxy.SodaByValue;

@SodaByValue
public class MaintenanceReport {

	private int id_;
	private String contents_;
	private String creatorId_;
	private byte[] qrData;

	public byte[] getQrData() {
		return qrData;
	}

	public void setQrData(byte[] qrData) {
		this.qrData = qrData;
	}

	public int getId() {
		return id_;
	}

	public void setId(int id) {
		id_ = id;
	}

	public String getContents() {
		return contents_;
	}

	public void setContents(String contents) {
		contents_ = contents;
	}

	public String getCreatorId() {
		return creatorId_;
	}

	public void setCreatorId(String creatorId) {
		creatorId_ = creatorId;
	}

}
