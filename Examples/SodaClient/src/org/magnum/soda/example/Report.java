package org.magnum.soda.example;

import java.io.Serializable;

import org.magnum.soda.proxy.SodaByValue;

@SodaByValue
public class Report implements Serializable{
		private String content;

		public Report(){
			
		}
		public Report(String s){
			content = s;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}


}