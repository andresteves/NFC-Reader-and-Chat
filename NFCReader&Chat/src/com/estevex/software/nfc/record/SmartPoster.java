/*
 * SmartPosterRecord.java
 *
 * Copyright 2011 by TIM w.e. All rights reserved.
 *
 * This software is the proprietary information of TIM w.e.
 * Use is subject to license terms.
 */
package com.estevex.software.nfc.record;

import java.nio.charset.Charset;
import java.util.Arrays;

import android.app.Activity;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.estevex.software.nfc.NdefMessageParser;

/**
 * An NFC Smart Poster Record
 */
public class SmartPoster implements ParsedNdefRecord {

    /**
     * NFC Forum Smart Poster Record Type Definition section 3.2.1.
     *
     * "The Title record for the service (there can be many of these in
     * different languages, but a language MUST NOT be repeated). This record is
     * optional."
     */
    private final TextRecord mTitleRecord;
    
    /** The TYPE type record */
	private static final byte[] TYPE_TYPE = new byte[] { 't' };
	
	/** The ACTION type record */
	private static final byte[] ACTION_RECORD_TYPE = new byte[] { 'a', 'c', 't' };
	
	/** Charset UTF-8 */
	public static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");

    /**
     * NFC Forum Smart Poster Record Type Definition section 3.2.1.
     *
     * "The URI record. This is the core of the Smart Poster, and all other
     * records are just metadata about this record. There MUST be one URI record
     * and there MUST NOT be more than one."
     */
    private final UriRecord mUriRecord;

    /**
     * NFC Forum Smart Poster Record Type Definition section 3.2.1.
     *
     * "The Type record. If the URI references an external entity (e.g., via a
     * URL), the Type record may be used to declare the MIME type of the entity.
     * This can be used to tell the mobile device what kind of an object it can
     * expect before it opens the connection. The Type record is optional."
     */
    private final String mType;

    private SmartPoster(UriRecord uri, TextRecord title, String type) {
        mUriRecord = uri;
        mTitleRecord = title;
        mType = type;
    }

    public UriRecord getUriRecord() {
        return mUriRecord;
    }

    /**
     * Returns the title of the smart poster. This may be {@code null}.
     */
    public TextRecord getTitle() {
        return mTitleRecord;
    }

    public static SmartPoster parse(NdefRecord record) {
    	NdefMessage subRecords = null;
		try {
			subRecords = new NdefMessage(record.getPayload());
		} catch (FormatException e) {
		}
		
		UriRecord uri = null;
		TextRecord title = null;
		String type = null;
		
		NdefRecord[] recordsRaw = subRecords.getRecords();
		//Iterable<ParsedNdefRecord> records = NdefMessageParser.getRecords(recordsRaw);
		
		// Get type and action
		for (NdefRecord ndefRecord : recordsRaw) {
			if (Arrays.equals(SmartPoster.TYPE_TYPE, record.getType())) {
				type = new String(ndefRecord.getPayload(),
						CHARSET_UTF_8);
			} else if (Arrays.equals(SmartPoster.ACTION_RECORD_TYPE, record.getType())) {
				
			}
		}
		
		return new SmartPoster(uri, title, type);
    }


    public static boolean isPoster(NdefRecord record) {
        try {
            parse(record);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public View getView(Activity activity, LayoutInflater inflater, ViewGroup parent, int offset) {
        if (mTitleRecord != null) {
            
        } else {
            // Just a URI, return a view for it directly
            return mUriRecord.getView(activity, inflater, parent, offset);
        }
		return parent;
    }

	@Override
	public String getType() {
		return new String(NdefRecord.RTD_SMART_POSTER);
	}
}
