/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.estevex.software.nfc;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

import com.estevex.software.nfc.record.ParsedNdefRecord;
import com.estevex.software.nfc.record.TextRecord;

/**
 * An {@link Activity} which handles a broadcast of a new tag that the device
 * just discovered.
 */
public class TagViewer extends Activity {

	static final String TAG = "ViewTag";

	/** The intent filter */
	private static final IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

	/** The intent filter array */
	private static final IntentFilter[] intentFiltersArray = new IntentFilter[] { tag };

	/** The tech list */
	private static final String[][] techListsArray = new String[][] { 
		new String[] { NfcF.class.getName() },
		new String[] { NfcA.class.getName() }, 
		new String[] { NfcV.class.getName() },
		new String[] { MifareClassic.class.getName() }, 
		new String[] { MifareUltralight.class.getName() },
		new String[] { IsoDep.class.getName() }, 
		new String[] { NfcB.class.getName() } };

	/** The NFC adapter */
	private NfcAdapter nfcAdapter;

	/** The pending intent */
	private PendingIntent pendingIntent;

	/**
	 * This activity will finish itself in this amount of time if the user
	 * doesn't do anything.
	 */
	static final int ACTIVITY_TIMEOUT_MS = 1 * 1000;

	TextView mTitle, mType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reader);
		mTitle = (TextView) findViewById(R.id.textView1);
		mType = (TextView) findViewById(R.id.tag_type);

		//resolveIntent(getIntent());
		// Set NFC adapter
		this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);

		// Create pending intent 
		this.pendingIntent = PendingIntent.getActivity(
				this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	}

	void resolveIntent(Intent intent) {
		// Parse the intent
		String action = intent.getAction();

		Log.i("ACTION", ""+action);

		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			// When a tag is discovered we send it to the service to be save. We
			// include a PendingIntent for the service to call back onto. This
			// will cause this activity to be restarted with onNewIntent(). At
			// that time we read it from the database and view it.
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] msgs;
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[] {};
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
				NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
				msgs = new NdefMessage[] {msg};

			}
			// Setup the views
			//setTitle(new String(msgs[0].getRecords()[0].getPayload()));
			buildTagViews(msgs);
		} else {
			Log.e(TAG, "Unknown intent " + intent);
			finish();
			return;
		}
	}

	void buildTagViews(NdefMessage[] msgs) {
		if (msgs == null || msgs.length == 0) {
			return;
		}
		// Parse the first message in the list
		// Build views for all of the sub records
		List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
		final int size = records.size();
		if(size == 0)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("TAG TNF NOT KNOWN!")
			.setCancelable(false)
			.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//
				}
			});
			builder.create().show();
		}else{
			for (int i = 0; i < size; i++) {
				ParsedNdefRecord record = records.get(i);
				TextRecord tr = (TextRecord) record;
				mTitle.setText(tr.getText());
				mType.setText(record.getType());				
			}
		}		
	}

	@Override
	public void onNewIntent(Intent intent) {
		//setIntent(intent);
		resolveIntent(intent);
	}

	/**
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		nfcAdapter.disableForegroundDispatch(this);
	}   

	/**
	 * @see android.app.Activity#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		try{
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
		}catch (Exception e){
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle.setText(title);
	}

	// Device Back button pressed
	public void onBackPressed() {
		// do something on back.
		Log.i("BACK BUTTON PRESSED", " --------------- BACK --------------- ");	
		Intent intent = new Intent(getApplicationContext(), Main.class);
		startActivity(intent);
		finish();
	}
}
