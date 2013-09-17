package org.magnum.soda.example.gcm.server.object;

import java.io.Serializable;
import java.util.UUID;

public class Report implements Serializable{
	
		private UUID id;
		private String content;
		
		public Report(String s){
			content = s;
			id = UUID.randomUUID();
		}
		
		public UUID getId() {
			return id;
		}
		
		public void setId(UUID id) {
			this.id = id;
		}
		
		public String getContent() {
			return content;
		}
		
		public void setContent(String content) {
			this.content = content;
		}
		
}
//27
