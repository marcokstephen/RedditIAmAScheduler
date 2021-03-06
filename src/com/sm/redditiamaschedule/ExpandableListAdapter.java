package com.sm.redditiamaschedule;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
		
		String[] files = context.fileList();
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
				String[] newFiles = context.fileList();
				if (Arrays.asList(newFiles).contains(name)){
					starImage.setImageResource(R.drawable.not_lit_star); //if it is saved, we delete it
					context.deleteFile(name);
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

						FileOutputStream fos = context.openFileOutput(name, Context.MODE_PRIVATE);
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
				    context.startActivity( browse );
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
				String[] timeArray = amaTime.split(":");
				hour = Integer.parseInt(timeArray[0]);
				minute = Integer.parseInt(timeArray[1].substring(0, 2));
				
				if (amaTime.endsWith("pm") && hour < 12){
					hour += 12;
				}
				
				Calendar startTime = Calendar.getInstance();
				startTime.set(year,month,day,hour,minute);
				
				Intent intent = new Intent(Intent.ACTION_INSERT);
				intent.setType("vnd.android.cursor.item/event");
				intent.putExtra(Events.TITLE, amaTitle + " AMA");
				intent.putExtra(Events.DESCRIPTION, amaDescription);
				intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime.getTimeInMillis());
				intent.setData(CalendarContract.Events.CONTENT_URI);
				context.startActivity(intent);
			}
		});
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}