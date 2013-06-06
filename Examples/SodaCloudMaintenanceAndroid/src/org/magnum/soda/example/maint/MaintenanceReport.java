/* 
 **
 ** Copyright 2013, Jules White
 **
 ** 
 */
package org.magnum.soda.example.maint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.magnum.soda.proxy.SodaByValue;

@SodaByValue
public class MaintenanceReport implements Serializable {

	private UUID id_;
	private String title_;
	private String contents_;
	private String creatorId_;
	private Date createTime_;
	private List<UserListener> follower_= new ArrayList<UserListener>();
	private byte[] qrData;
	
	private byte[] imageData;

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}
	
	public String getTitle() {
		return title_;
	}
	public void setTitle(String title) {
		this.title_ = title;
	}
	
	public byte[] getQrData() {
		return qrData;
	}

	public void setQrData(byte[] qrData) {
		this.qrData = qrData;
	}

	public UUID getId() {
		return id_;
	}

	public void setId(UUID id) {
		id_ = id;
	}

	public String getContents() {
		return contents_;
	}

	public void setContents(String contents) {
		contents_ = contents;
	}
	public Date getCreateTime_() {
		return createTime_;
	}

	public void setCreateTime_(Date createTime_) {
		this.createTime_ = createTime_;
	}

	public String getCreatorId() {
		return creatorId_;
	}

	public void setCreatorId(String creatorId) {
		creatorId_ = creatorId;
	}

}
