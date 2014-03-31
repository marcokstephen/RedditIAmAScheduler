package com.sm.redditiamaschedule;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
	
	public class ExpandableListAdapter extends BaseExpandableListAdapter {

		private Context context;
		private ArrayList<ExpandListGroup> groups;
		
		public ExpandableListAdapter(Context context,
				ArrayList<ExpandListGroup> groups) {
			//super();
			this.context = context;
			this.groups = groups;
		}

		@Override
		public int getGroupCount() {
			return groups.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			ArrayList<ExpandListChild> child = groups.get(groupPosition).getChild();
			return child.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groups.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			ArrayList<ExpandListChild> child = groups.get(groupPosition).getChild();
			return child.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			ExpandListGroup group = (ExpandListGroup) getGroup(groupPosition);
			if (convertView == null){
				LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inf.inflate(R.layout.list_item, null);
			}
			TextView listItemTV = (TextView)convertView.findViewById(R.id.nameTextView);
			listItemTV.setText(group.getName());
			
			TextView dateTV = (TextView)convertView.findViewById(R.id.dateTextView);
			TextView descTV = (TextView)convertView.findViewById(R.id.descriptionTextView);
			dateTV.setText(group.getDate());
			descTV.setText(group.getDescription());
			
			final ImageView starImage = (ImageView)convertView.findViewById(R.id.not_lit_star);
			
			String[] files = fileList();
			if (Arrays.asList(files).contains(group.getName())){
				starImage.setImageResource(R.drawable.lit_star);
			} else {
				starImage.setImageResource(R.drawable.not_lit_star);
			}
			
			final String name = group.getName();
			final String date = group.getDate();
			final String desc = group.getDescription();
			starImage.setOnClickListener(new View.OnClickListener ()
			{
				@Override
				public void onClick(View v) {
					String[] newFiles = fileList();
					if (Arrays.asList(newFiles).contains(name)){
						starImage.setImageResource(R.drawable.not_lit_star); //if it is saved, we delete it
						deleteFile(name);
					} else {
						starImage.setImageResource(R.drawable.lit_star); //if it isnt saved, we add it
						try {
							
							final String NEWLINE = "PARSE";
							String nameToWrite = name;
							String dateToWrite = date;
							String descToWrite = desc;
							if (nameToWrite.equals("")){
								nameToWrite = "null";
							}
							if (dateToWrite.equals("")){
								dateToWrite = "null";
							}
							if (descToWrite.equals("")){
								descToWrite = "null";
							}

							FileOutputStream fos = openFileOutput(name, Context.MODE_PRIVATE);
							fos.write(nameToWrite.getBytes());
							fos.write(NEWLINE.getBytes());
							fos.write(dateToWrite.getBytes());
							fos.write(NEWLINE.getBytes());
							fos.write(descToWrite.getBytes());
							fos.close();
							
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
			
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			ExpandListChild child = (ExpandListChild) getChild(groupPosition, childPosition);
			if (convertView == null){
				LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inf.inflate(R.layout.list_child, null);
			}
			final String url = child.getUrl();
			Button infoButton = (Button)convertView.findViewById(R.id.infoButton);
			infoButton.setVisibility(View.VISIBLE);
			if (url.equals("null")){
				infoButton.setVisibility(View.GONE);
			} else
			{
				infoButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						openWebURL(url);
					}
					
					public void openWebURL( String inURL ) {
					    Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );
					    startActivity( browse );
					}
				});
			}
			
			final String amaTime = child.getTime();
			final String amaDate = child.getDate();
			final String amaTitle = groups.get(groupPosition).getName();
			final String amaDescription = groups.get(groupPosition).getDescription();
			Button calendarButton = (Button)convertView.findViewById(R.id.calendarButton);
			calendarButton.setOnClickListener(new View.OnClickListener() { 
				@Override
				public void onClick(View v) {
					
					int day;
					int month;
					int year;
					Calendar calendar = Calendar.getInstance();
					int currentyear = calendar.get(Calendar.YEAR);
					int currentmonth = calendar.get(Calendar.MONTH);
					
					if (amaDate.substring(1,2).equals(" ")){
						day = Integer.parseInt(amaDate.substring(0,1));
						if (amaDate.substring(2,5).equals("Jan")){
							month = 0;
						} else if  (amaDate.substring(2,5).equals("Feb")){
							month = 1;
						} else if  (amaDate.substring(2,5).equals("Mar")){
							month = 2;
						} else if  (amaDate.substring(2,5).equals("Apr")){
							month = 3;
						} else if  (amaDate.substring(2,5).equals("May")){
							month = 4;
						} else if  (amaDate.substring(2,5).equals("Jun")){
							month = 5;
						} else if  (amaDate.substring(2,5).equals("Jul")){
							month = 6;
						} else if  (amaDate.substring(2,5).equals("Aug")){
							month = 7;
						} else if  (amaDate.substring(2,5).equals("Sep")){
							month = 8;
						} else if  (amaDate.substring(2,5).equals("Oct")){
							month = 9;
						} else if  (amaDate.substring(2,5).equals("Nov")){
							month = 10;
						} else if (amaDate.substring(2,5).equals("Dev")){
							month = 11;
						} else {
							month = currentmonth;
						}
					} else {
						day = Integer.parseInt(amaDate.substring(0,2));
						if (amaDate.substring(3,6).equals("Jan")){
							month = 0;
						} else if  (amaDate.substring(3,6).equals("Feb")){
							month = 1;
						} else if  (amaDate.substring(3,6).equals("Mar")){
							month = 2;
						} else if  (amaDate.substring(3,6).equals("Apr")){
							month = 3;
						} else if  (amaDate.substring(3,6).equals("May")){
							month = 4;
						} else if  (amaDate.substring(3,6).equals("Jun")){
							month = 5;
						} else if  (amaDate.substring(3,6).equals("Jul")){
							month = 6;
						} else if  (amaDate.substring(3,6).equals("Aug")){
							month = 7;
						} else if  (amaDate.substring(3,6).equals("Sep")){
							month = 8;
						} else if  (amaDate.substring(3,6).equals("Oct")){
							month = 9;
						} else if  (amaDate.substring(3,6).equals("Nov")){
							month = 10;
						} else if (amaDate.substring(3,6).equals("Dev")){
							month = 11;
						} else {
							month = currentmonth;
						}
					}
					
					if (month < currentmonth){
						year = currentyear+1;
					} else {
						year = currentyear;
					}
					
					int hour;
					int minute;
					Log.d("Alert","The current time is " + amaTime);
					if (amaTime.charAt(1) != '0' && amaTime.charAt(1) != '1' && amaTime.charAt(1) != '2'){
						hour = Integer.parseInt(amaTime.substring(0,1));
					} else {
						hour = Integer.parseInt(amaTime.substring(0,2));
					}
					
					if (amaTime.endsWith("pm") && hour < 12){
						hour += 12;
					}
					
					//assumes that an ama can either only start on the hour or the half hour, which is how mods have
					//currently been listing them
					//should be sufficient given the imprecise behaviour of scheduled authors anyways
					if (amaTime.contains(":")){
						minute = 30;
					} else {
						minute = 0;
					}
					
					Calendar startTime = Calendar.getInstance();
					startTime.set(year,month,day,hour,minute);
					
					Intent intent = new Intent(Intent.ACTION_INSERT);
					intent.setType("vnd.android.cursor.item/event");
					intent.putExtra(Events.TITLE, amaTitle + " AMA");
					intent.putExtra(Events.DESCRIPTION, amaDescription);
					intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.getTimeInMillis());
					intent.setData(CalendarContract.Events.CONTENT_URI);
					startActivity(intent);
				}
			});
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
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

		// Inflate the menu; this adds items to the action bar if it is present.
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
    	
    	if (hour >= 12){
    		if (hour != 12) hour -= 12;
    		date = day + " " + month + " " + hour + ":" + minute + "0pm";
    	} else {
    		if (hour == 0) hour = 12;
    		date = day + " " + month + " " + hour + ":" + minute + "0am";
    	}
    	
		return date;
    	
    }
}