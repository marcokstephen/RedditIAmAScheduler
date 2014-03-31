package com.sm.redditiamaschedule;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class Watchlist extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_watchlist);
		
		drawView();
	}
	
	public void drawView(){
		String[] persons = fileList();
		ListView myListView = new ListView(this);
		TextView noticeTV = new TextView(this);
		//TextView noticeTV = (TextView)findViewById(R.id.file_output);
		if (persons.length == 0){
			noticeTV.setVisibility(View.VISIBLE);
			noticeTV.setText("No saved AMAs!");
			noticeTV.setGravity(Gravity.CENTER);
			setContentView(noticeTV);
		} else {
			String[] outputStrings = new String[persons.length];
			noticeTV.setVisibility(View.GONE);
			Log.d("Alert","About to add first element to list");
			for(int i = 0; i < persons.length; i++){
				String FILE = persons[i];
				String value = "";
				
				try {
					FileInputStream fis = openFileInput(FILE);
					byte[] input = new byte[fis.available()];
					while(fis.read(input) != -1){
						value += new String(input);
					}
					fis.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e){
					e.printStackTrace();
				}
				outputStrings[i] = value;
			}//end for loop
			
			SavedListAdapter adapter = new SavedListAdapter(outputStrings, Watchlist.this);
			//ListView savedLV = (ListView)findViewById(R.id.savedListView);
			myListView.setAdapter(adapter);
			setContentView(myListView);
		}//end if length != 0
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.watchlist, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.clear_saved) {
			clearSaved();
			return true;
		} else if (id == R.id.refresh_saved){
			drawView();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void clearSaved(){
		String[] persons = fileList();
		for (int i = 0; i < persons.length; i++){
			deleteFile(persons[i]);
		}
		drawView();
	}
}
