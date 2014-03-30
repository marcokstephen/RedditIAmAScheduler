package com.sm.redditiamaschedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
		final ImageView saveStar = (ImageView)convertView.findViewById(R.id.saved_star_icon);
		final int currentPosition = position;
		saveStar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				saveStar.setImageResource(R.drawable.not_lit_star);
			}
		});
		
		String output = myList[position];
		String[] oArray = output.split("PARSE");
		savedName.setText(oArray[0]);
		savedTime.setText(oArray[1]);
		savedDesc.setText(oArray[2]);
		savedName.setVisibility(View.VISIBLE);
		savedTime.setVisibility(View.VISIBLE);
		savedDesc.setVisibility(View.VISIBLE);
		
		if (oArray[0].equals("null")){
			savedName.setVisibility(View.GONE);
			savedTime.setVisibility(View.GONE);
			savedDesc.setVisibility(View.GONE);
		}
		
		if (oArray[1].equals("null")){
			savedTime.setVisibility(View.GONE);
		}
		
		if (oArray[2].equals("null")){
			savedDesc.setVisibility(View.GONE);
		}

		return convertView;
	}	
}
