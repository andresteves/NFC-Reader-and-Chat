package com.estevex.software.nfc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendReport extends Activity{
	
	EditText subj, content;
	Button send;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.error_report);
        
        subj = (EditText) findViewById(R.id.subject);
        content = (EditText) findViewById(R.id.content);
        send = (Button) findViewById(R.id.send_b);
        
        send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_EMAIL, new String[]{"fkh009@gmail.com"});
				i.putExtra(Intent.EXTRA_SUBJECT, subj.getText().toString());
				i.putExtra(Intent.EXTRA_TEXT   , content.getText().toString());
				try {
				    startActivity(Intent.createChooser(i, "Send mail..."));
				} catch (android.content.ActivityNotFoundException ex) {
				    Toast.makeText(SendReport.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
