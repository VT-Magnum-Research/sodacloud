package org.magnum.soda.examples;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Report implements Serializable{
		private String content;
		
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
