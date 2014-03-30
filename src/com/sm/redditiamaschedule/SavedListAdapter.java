package com.sm.redditiamaschedule;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SavedListAdapter extends BaseAdapter {

	private Context context;
	private String[] myList;
	private LayoutInflater myInflater;
	
	public SavedListAdapter (String[] list, Context c){
		context = c;
		myList = list;
		myInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return myList.length;
	}
	@Override
	public Object getItem(int position) {
		return myList[position];
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null){
			convertView = myInflater.inflate(R.layout.saved_listview_element, null);
		}
		TextView savedName = (TextView)convertView.findViewById(R.id.saved_name_textview);
		TextView savedTime = (TextView)convertView.findViewById(R.id.saved_time_textview);
		TextView savedDesc = (TextView)convertView.findViewById(R.id.saved_desc_textview);
		
		String output = myList[position];
		String[] oArray = output.split("PARSE");
		savedName.setText(oArray[0]);
		savedTime.setText(oArray[1]);
		savedDesc.setText(oArray[2]);

		return convertView;
	}
	
	
}
