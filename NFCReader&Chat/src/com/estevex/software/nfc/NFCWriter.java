package com.estevex.software.nfc;

import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.estevex.software.nfc.record.TextRecord;

public class NFCWriter extends Activity{

	/** The intent filter */
	private static final IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

	/** The intent filter array */
	private static final IntentFilter[] intentFiltersArray = new IntentFilter[] {tag};

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

	/** The tag text */
	private EditText writer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.writer);

		writer = (EditText) findViewById(R.id.editText1);

		// Set NFC adapter
		this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);

		// Create pending intent 
		this.pendingIntent = PendingIntent.getActivity(
				this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
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
		}catch (Exception e){}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		// Get tag
		final Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		//		String text = writer.getText().toString();

		boolean wrote = writeTag(tag, intent);

		if(wrote){
			// Show result message
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("TAG Written with Success!")
			.setCancelable(false)
			.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//
				}
			});
			builder.create().show();
		}
		else{
			// Show result message
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("TAG Written Failure!")
			.setCancelable(false)
			.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//
				}
			});
			builder.create().show();
		}
	}

	public boolean writeTag(Tag tag, Intent intent){
		NdefFormatable formatableTag = NdefFormatable.get(tag);

		//verify if tag is formatable
		if (formatableTag != null) { //NOT FORMATED	
			// Get tag text
			String tagText = this.writer.getText().toString();

			// Create NDEF record
			NdefRecord textRecord = TextRecord.newTextRecord(tagText, 
					new Locale("en", "EN"), true);

			// Create NDEF message
			NdefMessage ndefMessage = new NdefMessage(
					new NdefRecord[] { textRecord });

			try {
				formatableTag.connect();
				formatableTag.format(ndefMessage);
				formatableTag.close();
				return true;
			} catch (Exception e) {
			}

		}
		else{ //FORMATED
			// Get tag text
			String tagText = this.writer.getText().toString();

			// Create NDEF record
			NdefRecord textRecord = TextRecord.newTextRecord(tagText, 
					new Locale("en", "EN"), true);

			// Create NDEF message
			NdefMessage ndefMessage = new NdefMessage(
					new NdefRecord[] { textRecord });

			Ndef ndef = Ndef.get(tag);
			try {
				ndef.connect();
				ndef.writeNdefMessage(ndefMessage);
				return true;
			} catch (Exception e) {
			}	
		}
		return false;
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
