package org.magnum.soda.example.maint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity{
	// UI references.
	private Button viewReportButton;
	private Button createReportButton;
	private Button createWatchZoneButton;
    private String[] viewReportMenuItem= new String[]{"by location","by QR code"};
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		viewReportButton = (Button)findViewById(R.id.viewReportButton);
		createReportButton = (Button)findViewById(R.id.createReportButton);
		createWatchZoneButton = (Button)findViewById(R.id.createWatchZone_button);
		
		viewReportButton.setOnClickListener(new AlertClickListener()); 
		
		createReportButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CreateReportIntent();
			}
		});
		
		createWatchZoneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CreateWatchZoneIntent();
			}
		});
	}
	
    private void CreateReportIntent(){
    	Intent i =new Intent(this, CreateReportActivity.class);
		startActivity(i);
    }
    
    private void CreateWatchZoneIntent(){
    	Intent i =new Intent(this, WatchZoneActivity.class);
		startActivity(i);
    }
    
    private void SearchByLocationIntent(){
    	Intent i =new Intent(this, SearchByLocationActivity.class);
		startActivity(i);
    }
    
    private void SearchByQRIntent(){
    	Intent i =new Intent(this, SearchByQRActivity.class);
		startActivity(i);
    }
    
    public class AlertClickListener implements OnClickListener{  
    	  @Override  
    	  public void onClick(View v) {  
    	   new AlertDialog.Builder(MainActivity.this).setTitle("Search report").setItems(viewReportMenuItem,new DialogInterface.OnClickListener(){  
    	      public void onClick(DialogInterface dialog, int which){  
    	       Toast.makeText(MainActivity.this, "I choose to search: " + viewReportMenuItem[which],Toast.LENGTH_LONG).show();  
    	       dialog.dismiss();  
    	       if(which==0){
    	    	   SearchByLocationIntent();
    	       }
    	       else if(which==1){
    	    	   SearchByQRIntent();
    	       }
    	      }  
    	   }).show();  
    	  }
    }
	
}
