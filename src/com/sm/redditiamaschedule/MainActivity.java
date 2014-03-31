package com.sm.redditiamaschedule;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

public class MainActivity extends Activity {
	ArrayList<ExpandListGroup> list = new ArrayList<ExpandListGroup>();
	ArrayList<ExpandListChild> list2 = new ArrayList<ExpandListChild>();
	String internetAvailableNotice = "";
	View LoadingView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		new GetList().execute();
		
        if(!isNetworkAvailable()){
        	internetAvailableNotice = "There is no available internet connection!";
            new AlertDialog.Builder(this)
    	    .setTitle("Network Required!")
    	    .setMessage(internetAvailableNotice)
    	    .setCancelable(true)
    	    .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
    	    	public void onClick(DialogInterface dialog, int which) { 
    	    		dialog.cancel();
    	    	}
    	    })
    	    .setPositiveButton("Wi-Fi Settings", new DialogInterface.OnClickListener() {
    	    	public void onClick(DialogInterface dialog, int which) { 
    	    		startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
    	    	}
    	    })
    	    .show();
        }
	}
	
	//returns true if there is connection, returns false if there is no connection
    public boolean isNetworkAvailable(){
    	ConnectivityManager cm = (ConnectivityManager)
    			getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    	//if no network is available, networkInfo will be null
    	if (networkInfo != null && networkInfo.isConnected()){
    		return true;
    	}
    	return false;
    }
	
	public void refresh() {
	  new GetList().execute();
	}
	
	public class GetList extends AsyncTask<Void,Void,Void>{
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			ProgressBar loadingBar = (ProgressBar)findViewById(R.id.loading_bar);
			loadingBar.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			String date, time, name, desc, url;
			
			Log.d("Alert","Starting to parse data");
			ExpandListGroup group = new ExpandListGroup();
			ExpandListChild child = new ExpandListChild();
			
			try{
				Document doc = Jsoup.connect("http://www.reddit.com/r/iama").get();
				//This code assumes that the schedule table is the first HTML table on the page
				Element table = doc.getElementsByTag("table").first();
				Element tbody = table.getElementsByTag("tbody").first();
				Elements tableRows = tbody.getElementsByTag("tr");
				
				//Date - Time - Person - Description
				for (Element tableRow : tableRows){
					Elements tdRows = tableRow.getElementsByTag("td");
					Element infoLink = tableRow.getElementsByTag("a").first();

					date = tdRows.get(0).text().toString();
					time = tdRows.get(1).text().toString();
					name = tdRows.get(2).text().toString();
					desc = tdRows.get(3).text().toString();
					if (infoLink != null){
						url = infoLink.attr("abs:href").toString();
					} else {
						url = "null";
					}

					if (name.contains("/")){
						//linux illegal filename character
						//we use the name to save files
						//if a user wishes to bookmark an AMA
						name = name.replace("/"," - ");
					}
					
					String combinedDate = date+", " + time;
					combinedDate = modifyDate(combinedDate);
					String[] newTimeArray = combinedDate.split(" ");
					time = newTimeArray[2];
					
					group = new ExpandListGroup();
					group.setDate(combinedDate);
					group.setName(name);
					group.setDescription(desc);
					
					list2 = new ArrayList<ExpandListChild>();
					child = new ExpandListChild();
					child.setDate(combinedDate);
					child.setUrl(url);
					child.setTime(time);
					list2.add(child);
					
					group.setChild(list2);
					list.add(group);
				}
				
			}catch(IOException e){
				System.out.println("Could not connect: " + e);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			super.onPostExecute(result);
			ProgressBar loadingBar = (ProgressBar)findViewById(R.id.loading_bar);
			loadingBar.setVisibility(View.GONE);
			
			ExpandableListView elv = (ExpandableListView)findViewById(R.id.elv1);
			ExpandableListAdapter adapter = new ExpandableListAdapter(MainActivity.this, list);
			elv.setAdapter(adapter);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}	
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch (item.getItemId()){
    	case R.id.refresh_option:
    		refresh();
    		break;
    	case R.id.help_menu:
    		showHelp();
    		break;
    	case R.id.action_settings:
    		showSettings();
    		break;
    	case R.id.star_option:
    		showStar();
    		break;
    	default:
    		break;
    	}
    	return true;
    }
    
    public void showHelp(){
    	Intent intent = new Intent(this, Help.class);
    	startActivity(intent);
    }
    
    public void showSettings(){
    	Intent intent = new Intent(this, Prefs.class);
    	startActivity(intent);
    }
    
    public void showStar(){
    	Intent intent = new Intent(this, Watchlist.class);
    	startActivity(intent);
    }
    public String modifyDate(String date){
    	String[] dateArray = date.split(" ");
    	int day = Integer.parseInt(dateArray[0]);
    	String month = dateArray[1];
    	String time = dateArray[2];
    	
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	int timeZone = Integer.parseInt(prefs.getString("listTimeZone", "0"));
    	boolean twentyFourHour = prefs.getBoolean("24hour", false);
    	int hour = 0;
    	int minute = 0;

    	if (time.charAt(1) == '0' || time.charAt(1) == '1' || time.charAt(1) == '2'){
    		//time is a 2 digit number
    		hour = Integer.parseInt(time.substring(0,2));
    		if (time.charAt(2) == ':'){
    			minute = 3;
    		}
    	} else {
    		hour = Integer.parseInt(time.substring(0,1));
    		if (time.charAt(1) == ':'){
    			minute = 3;
    		}
    	}
    	
    	if (time.endsWith("pm") && hour < 12){
    		hour += 12;
    	}
    	
    	if (timeZone < 0){
    		if (timeZone%2 != 0){
    			minute += 3;
    			hour--;
    		}
    		timeZone /= 2;
    	} else {
    		if (timeZone%2 != 0){
    			minute += 3;
    		}
    		timeZone /= 2;
    	}
    	//implementing the timezone change
    	hour = hour + timeZone;
    	if (minute == 6){
    		minute -= 6;
    		hour++;
    	}
    	//we now have the initial values for hour and minute
    	
    	if (hour > 0){
    		while (hour >= 24){
    			hour -= 24;
    			day++;
    			
    			if (day > 31 && month.startsWith("Jan")){
    				day = 1;
    				month = "Feb,";
    			} else if (day > 28 && month.startsWith("Feb")){
    				day = 1;
    				month = "Mar,";
    			} else if (day > 31 && month.startsWith("Mar")){
    				day = 1;
    				month = "Apr,";
    			} else if (day > 30 && month.startsWith("Apr")){
    				day = 1;
    				month = "May,";
    			} else if (day > 31 && month.startsWith("May")){
    				day = 1;
    				month = "June,";
    			} else if (day > 30 && month.startsWith("Jun")){
    				day = 1;
    				month = "July,";
    			} else if (day > 31 && month.startsWith("Jul")){
    				day = 1;
    				month = "Aug,";
    			} else if (day > 31 && month.startsWith("Aug")){
    				day = 1;
    				month = "Sep,";
    			} else if (day > 30 && month.startsWith("Sep")){
    				day = 1;
    				month = "Oct,";
    			} else if (day > 31 && month.startsWith("Oct")){
    				day = 1;
    				month = "Nov,";
    			} else if (day > 30 && month.startsWith("Nov")){
    				day = 1;
    				month = "Dec,";
    			} else if (day > 31 && month.startsWith("Dec")){
    				day = 1;
    				month = "Jan,";
    			}
    		}
    	} else {
    		while (hour < 1){
    			hour += 24;
    			day--;
    			if (day < 1 && month.startsWith("Jan")){
    				day = 31;
    				month = "Dec,";
    			} else if (day < 1 && month.startsWith("Feb")){
    				day = 31;
    				month = "Jan,";
    			} else if (day < 1 && month.startsWith("Mar")){
    				day = 28;
    				month = "Feb";
    			} else if (day < 1 && month.startsWith("Apr")){
    				day = 31;
    				month = "Mar,";
    			} else if (day < 1 && month.startsWith("May")){
    				month = "Apr,";
    				day = 30;
    			} else if (day < 1 && month.startsWith("Jun")){
    				month = "May,";
    				day = 31;
    			} else if (day < 1 && month.startsWith("Jul")){
    				month = "Jun,";
    				day = 30;
    			} else if (day < 1 && month.startsWith("Aug")){
    				month = "Jul,";
    				day = 31;
    			} else if (day < 1 && month.startsWith("Sep")){
    				month = "Aug,";
    				day = 31;
    			} else if (day < 1 && month.startsWith("Oct")){
    				month = "Sep,";
    				day = 30;
    			} else if (day < 1 && month.startsWith("Nov")){
    				month = "Oct,";
    				day = 31;
    			} else if (day < 1 && month.startsWith("Dec")){
    				month = "Nov,";
    				day = 30;
    			}
    		}
    	}
    	
    	if (twentyFourHour){
    		date = day + " " + month + " " + hour + ":" + minute + "0";
    	} else if (hour >= 12){
    		if (hour != 12) hour -= 12;
    		date = day + " " + month + " " + hour + ":" + minute + "0pm";
    	} else {
    		if (hour == 0) hour = 12;
    		date = day + " " + month + " " + hour + ":" + minute + "0am";
    	}
		return date;
    }
}