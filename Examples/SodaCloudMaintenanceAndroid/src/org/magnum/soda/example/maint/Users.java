package org.magnum.soda.example.maint;

import java.util.List;

import org.magnum.soda.Callback;
import org.magnum.soda.Soda;
import org.magnum.soda.proxy.SodaAsync;



import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.magnum.soda.Callback;
import org.magnum.soda.Soda;
import org.magnum.soda.SodaQuery;
import org.magnum.soda.ctx.SodaQR;
import org.magnum.soda.proxy.SodaAsync;

public interface Users {

	public static final String SVC_NAME = "User";
	
	public void addUser(User r);
	public List<User> getUsers();
		
	@SodaAsync
	public void getUsers(Callback<List<User>> callback);	
	
	public void addListener(UserListener l);
	public void removeListener(UserListener l);
	
}
