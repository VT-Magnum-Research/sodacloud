package org.magnum.soda.example.gcm.client.object;

import java.util.UUID;

public class ReportsListener {
	
	private String id;
	
	public ReportsListener(String id){
		this.id = id;
	}
	
	public ReportsListener(){
		id = UUID.randomUUID().toString();
	}
	
	public String getID(){
		return id.toString();
	}
	
	public void reportAdded(Report r){	
	}

	public void reportchanged(Report r){		
	}
	
}
//23