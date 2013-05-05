package org.magnum.soda.example.maint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.magnum.soda.Callback;

public class UsersImpl implements Users {

	private List<UserListener> listeners_ = new LinkedList<UserListener>();
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
	@Override
	public void getUsers(Callback<List<User>> callback) {
		// TODO Auto-generated method stub
		callback.handle(users_);
		
	}
	@Override
	public void addListener(UserListener l) {
		// TODO Auto-generated method stub
		listeners_.add(l);
		
		
	}
	@Override
	public void removeListener(UserListener l) {
		// TODO Auto-generated method stub
		listeners_.remove(l);
		
	}
}
