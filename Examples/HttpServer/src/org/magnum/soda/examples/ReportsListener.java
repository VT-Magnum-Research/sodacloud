package org.magnum.soda.examples;

import java.util.UUID;

//observer
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
		String message = id + "," + r.getContent();
		HttpServer.messageQueue.add(message);
	}

	public void reportchanged(Report r){
		
	}
	
}
//26