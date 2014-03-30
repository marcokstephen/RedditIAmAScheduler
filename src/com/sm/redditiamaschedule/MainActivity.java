package com.sm.redditiamaschedule;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

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
				LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
				convertView = inf.inflate(R.layout.list_item, null);
			}
			TextView listItemTV = (TextView)convertView.findViewById(R.id.nameTextView);
			listItemTV.setText(group.getName());
			
			TextView dateTV = (TextView)convertView.findViewById(R.id.dateTextView);
			TextView descTV = (TextView)convertView.findViewById(R.id.descriptionTextView);
			dateTV.setText(group.getDate());
			descTV.setText(group.getDescription());
			
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			ExpandListChild child = (ExpandListChild) getChild(groupPosition, childPosition);
			if (convertView == null){
				LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
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
			
			Button saveButton = (Button)convertView.findViewById(R.id.saveButton);
			saveButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String FILENAME = amaTitle;
					String NEWLINE = "PARSE";
					try {
						FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
						fos.write(amaTitle.getBytes());
						fos.write(NEWLINE.getBytes());
						fos.write(amaDate.getBytes());
						fos.write(NEWLINE.getBytes());
						fos.write(amaDescription.getBytes());
						fos.close();
						
						Context context = getApplicationContext();
						CharSequence text = "AMA Saved";
						int duration = Toast.LENGTH_SHORT;
						Toast toast = Toast.makeText(context, text, duration);
						toast.show();
						
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
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
					String combinedDate = date+", " + time;
					
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
}