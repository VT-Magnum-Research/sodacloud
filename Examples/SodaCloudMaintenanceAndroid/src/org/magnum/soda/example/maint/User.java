package org.magnum.soda.example.maint;
import org.magnum.soda.proxy.SodaByValue;

@SodaByValue
public class User {

	private String username_;
	private String pwd_;
	private String name_;
	private String phone_;
	
	
	public String getUsername_() {
		return username_;
	}
	public void setUsername_(String username_) {
		this.username_ = username_;
	}
	
	public String getPwd_() {
		return pwd_;
	}
	public void setPwd_(String pwd_) {
		this.pwd_ = pwd_;
	}
	public String getName_() {
		return name_;
	}
	public void setName_(String name_) {
		this.name_ = name_;
	}
	public String getPhone_() {
		return phone_;
	}
	public void setPhone_(String phone_) {
		this.phone_ = phone_;
	}



}