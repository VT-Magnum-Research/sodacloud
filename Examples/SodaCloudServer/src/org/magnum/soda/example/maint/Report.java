package org.magnum.soda.example.maint;

import java.util.ArrayList;
import java.util.List;

import org.magnum.soda.proxy.SodaByValue;

@SodaByValue
public class Report {
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
