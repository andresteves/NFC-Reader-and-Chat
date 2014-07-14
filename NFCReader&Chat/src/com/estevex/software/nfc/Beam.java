package com.estevex.software.nfc;

import java.nio.charset.Charset;
import java.util.Locale;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.format.Time;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Beam extends Activity{
	NfcAdapter mNfcAdapter;
	TextView mInfoText;
	EditText writeText;
	PendingIntent mNfcPendingIntent;
	IntentFilter[] mNdefExchangeFilters;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beamer);

		mInfoText = (TextView) findViewById(R.id.mInfo);
		writeText = (EditText) findViewById(R.id.textWrite);

		// Check for available NFC Adapter
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {
			mInfoText = (TextView) findViewById(R.id.mInfo);
			mInfoText.setText("NFC is not available on this device.");
		}

		mNfcPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		// Intent filters for exchanging over p2p.
		IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefDetected.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			Toast.makeText(getApplicationContext(), "Error Exception", Toast.LENGTH_SHORT).show();
		}
		mNdefExchangeFilters = new IntentFilter[] { ndefDetected, };
	}

	private void enableNdefExchangeMode() {
		mNfcAdapter.enableForegroundNdefPush(Beam.this,writerMessage());
		mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent,
				mNdefExchangeFilters, null);
		Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
	}

	public NdefMessage writerMessage()
	{
		Time time = new Time();
		time.setToNow();
		String text = ("-> " + writeText.getText().toString()+ " - " + time.format("%H:%M:%S"));
		NdefMessage msg = new NdefMessage(new NdefRecord[]{newTextRecord(text, Locale.ENGLISH, true)});

		return msg;
	}

	public static NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUtf8) {
		byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));

		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset.forName("UTF-16");
		byte[] textBytes = text.getBytes(utfEncoding);

		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		char status = (char) (utfBit + langBytes.length);

		byte[] data = new byte[1 + langBytes.length + textBytes.length]; 
		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
	}

	//	/**
	//	 * Implementation for the CreateNdefMessageCallback interface
	//	 */
	//	@Override
	//	public NdefMessage createNdefMessage(NfcEvent event) {
	//		Time time = new Time();
	//		time.setToNow();
	//		String text = ("-> " + writeText.getText().toString()+ " - " + time.format("%H:%M:%S"));
	//		NdefMessage msg = new NdefMessage(
	//				new NdefRecord[] { createMimeRecord(
	//						"application/com.example.android.beam", text.getBytes())
	//						/**
	//						 * The Android Application Record (AAR) is commented out. When a device
	//						 * receives a push with an AAR in it, the application specified in the AAR
	//						 * is guaranteed to run. The AAR overrides the tag dispatch system.
	//						 * You can add it back in to guarantee that this
	//						 * activity starts when receiving a beamed message. For now, this code
	//						 * uses the tag dispatch system.
	//						 */
	//						,NdefRecord.createApplicationRecord("com.example.android.beam")
	//				});
	//		return msg;
	//	}


//	/** This handler receives a message from onNdefPushComplete */
//	private final Handler mHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case MESSAGE_SENT:
//				Toast.makeText(getApplicationContext(), "Message sent!", Toast.LENGTH_LONG).show();
//				break;
//			}
//		}
//	};

	@Override
	public void onResume() {
		super.onResume();
		enableNdefExchangeMode();
	}
	
	@Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) 
        	mNfcAdapter.disableForegroundNdefPush(this);
    }

	@Override
	public void onNewIntent(Intent intent) {
		// onResume gets called after this to handle the intent
		processIntent(intent);
	}

	/**
	 * Parses the NDEF Message from the intent and prints to the TextView
	 */
	void processIntent(Intent intent) {		
		// NDEF exchange mode
	    if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
	    	Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
					NfcAdapter.EXTRA_NDEF_MESSAGES);
			// only one message sent during the beam
			NdefMessage msg = (NdefMessage) rawMsgs[0];	   
			// record 0 contains the MIME type, record 1 is the AAR, if present
			mInfoText.setText(new String(msg.getRecords()[0].getPayload()));
	    }	
	}
}