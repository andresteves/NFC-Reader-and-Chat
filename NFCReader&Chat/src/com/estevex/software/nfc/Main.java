package com.estevex.software.nfc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

public class Main extends Activity{

	Button read,write,sendEmail,writeDevice;
	CheckBox turnNfc;
	NfcAdapter nfcAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		read = (Button) findViewById(R.id.button1);		
		write = (Button) findViewById(R.id.button2);
		sendEmail = (Button) findViewById(R.id.button3);
		writeDevice = (Button) findViewById(R.id.button4);
		turnNfc = (CheckBox) findViewById(R.id.turn_nfc);
		
		turnNfc.setChecked(true);

		nfcAdapter = NfcAdapter.getDefaultAdapter(this);

		read.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(turnNfc.isChecked())
				{
					startActivity(new Intent(Main.this, TagViewer.class));
					finish();
				}else{
					// Show result message
					AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
					builder.setMessage("Turn NFC on!")
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
					builder.create().show();
				}

			}
		});

		write.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(turnNfc.isChecked())
				{
					startActivity(new Intent(Main.this, NFCWriter.class));
					finish();
				}else{
					// Show result message
					AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
					builder.setMessage("Turn NFC on!")
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
					builder.create().show();
				}
			}
		});

		sendEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(Main.this, SendReport.class));
				finish();
			}
		});
		
		writeDevice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(turnNfc.isChecked())
				{
					startActivity(new Intent(Main.this, Beam.class));
					finish();
				}else{
					// Show result message
					AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
					builder.setMessage("Turn NFC on!")
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});
					builder.create().show();
				}
			}
		});
	}

}
