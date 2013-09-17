package org.magnum.soda.example.maint;
import java.util.ArrayList;
import java.util.List;

import org.magnum.soda.proxy.SodaByValue;

@SodaByValue
public class User {

	private String username_;
	private String pwd_;
	private String name_;
	private String phone_;
	/*private List<String> following_= new ArrayList<String>();;
	
	public void addfollowing(String username){
		following_.add(username);
	}
	public void removefollowing(String username){
		following_.remove(username);
	}
	public boolean isfollowing(String username){
		return following_.contains(username);
	}*/
	
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