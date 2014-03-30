package com.sm.redditiamaschedule;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

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
			saveStar.setVisibility(View.GONE);
		}
		
		if (oArray[1].equals("null")){
			savedTime.setVisibility(View.GONE);
		}
		
		if (oArray[2].equals("null")){
			savedDesc.setVisibility(View.GONE);
		}
		
		final String nameToDelete = oArray[0];
		final String dateToDelete = oArray[1];
		final String descToDelete = oArray[2];
		saveStar.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String[] files = context.fileList();
				if (Arrays.asList(files).contains(nameToDelete))
				{
					saveStar.setImageResource(R.drawable.not_lit_star);
					context.deleteFile(nameToDelete);
				} else {
					saveStar.setImageResource(R.drawable.lit_star);
					try {
						
						final String NEWLINE = "PARSE";
						String nameToWrite = nameToDelete;
						String dateToWrite = dateToDelete;
						String descToWrite = descToDelete;
						if (nameToWrite.equals("")){
							nameToWrite = "null";
						}
						if (dateToWrite.equals("")){
							dateToWrite = "null";
						}
						if (descToWrite.equals("")){
							descToWrite = "null";
						}

						FileOutputStream fos = context.openFileOutput(nameToWrite, Context.MODE_PRIVATE);
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
}
