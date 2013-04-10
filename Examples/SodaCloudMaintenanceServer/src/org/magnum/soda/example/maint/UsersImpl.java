package org.magnum.soda.example.maint;

import java.util.ArrayList;
import java.util.List;

public class UsersImpl {
	
	private List<User> users_ = new ArrayList<User>();
	
	public void addUser(User r) {
		System.out.println("content :"+r.getUsername_()+" :"+r.getPwd_());
		users_.add(r);

	}
	public boolean hasUser(String username){
		for(User u:users_){
			if(u.getUsername_().equals(username))
				return true;
			else
				return false;
		}
		return false;
	}
	
	public User findUser(String username){
		for(User u:users_){
			if(u.getUsername_().equals(username))
				return u;
		}
		throw new IllegalArgumentException();
	}
	
	public List<User>getUsers(){
		return users_;
	}
}
