package com.sm.redditiamaschedule;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class Help extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		 TextView t2 = (TextView) findViewById(R.id.file_output);
		 t2.setMovementMethod(LinkMovementMethod.getInstance());
	}
}
