package com.sm.redditiamascheduler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	private List<Person> myPersons = new ArrayList<Person>();
	String internetAvailableNotice = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
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

        new GetList().execute();
	}
	
	public class GetList extends AsyncTask<Void,Void,Void>{
		
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
		}
		
		@Override
		protected Void doInBackground(Void... arg0) {
			String date, time, name, desc, url;
			
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
					myPersons.add(new Person(name,date,time,desc,url));
				}
				
			
			}catch(IOException e){
				System.out.println("Could not connect: " + e);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			super.onPostExecute(result);
			
			ArrayAdapter<Person> adapter = new myListAdapter();
			ListView list = (ListView)findViewById(android.R.id.list);
			list.setAdapter(adapter);
		}
		
		private class myListAdapter extends ArrayAdapter<Person>{
			public myListAdapter(){
				super(MainActivity.this, R.layout.person_list_view, myPersons);
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View itemView = convertView;
				if (itemView == null){
					itemView = getLayoutInflater().inflate(R.layout.person_list_view, parent, false);
				}
				//find the person to work with
				Person currentPerson = myPersons.get(position);
				
				//populate the listview
				TextView nameTV = (TextView)itemView.findViewById(R.id.textName);
				nameTV.setText(currentPerson.getName());
				String dateAndTime = currentPerson.getDay() +", " + currentPerson.getTime();
				TextView dateTV = (TextView)itemView.findViewById(R.id.textDate);
				dateTV.setText(dateAndTime);
				TextView descTV = (TextView)itemView.findViewById(R.id.textDescription);
				descTV.setText(currentPerson.getDescription());
				
				return itemView;
			}
			
			
		}
		
	}
	
	
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

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}