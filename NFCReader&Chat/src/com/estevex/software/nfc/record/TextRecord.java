/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.estevex.software.nfc.record;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;

import android.app.Activity;
import android.nfc.NdefRecord;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * An NFC Text Record
 */
public class TextRecord implements ParsedNdefRecord {

	/** Charset US-ASCII */
	public static final Charset CHARSET_US_ASCII = Charset.forName("US-ASCII");
	
	/** Charset UTF-8 */
	public static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");

	/** Charset UTF-16 */
	public static final Charset CHARSET_UTF_16 = Charset.forName("UTF-16");
	
    /** ISO/IANA language code */
    private final String mLanguageCode;

    private final String mText;

    private TextRecord(String languageCode, String text) {
    	this.mLanguageCode = languageCode;
		this.mText = text;
    }

    public String getText() {
        return mText;
    }

    /**
     * Returns the ISO/IANA language code associated with this text element.
     */
    public String getLanguageCode() {
        return mLanguageCode;
    }

    // TODO: deal with text fields which span multiple NdefRecords
    public static TextRecord parse(NdefRecord record) {
    	String languageCode, text;
        byte[] payload = record.getPayload();
		String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
		int languageCodeLength = payload[0] & 0077;
		
		try {
			languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
			text = new String(payload, languageCodeLength + 1, payload.length
					- languageCodeLength - 1, textEncoding);
		} catch (UnsupportedEncodingException e) {
			// should never happen unless we get a malformed tag.
			throw new IllegalArgumentException(e);
		}

		return new TextRecord(languageCode, text);
    }

    public static boolean isText(NdefRecord record) {
        try {
            parse(record);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    public static NdefRecord newTextRecord(String text, Locale locale, boolean encodeInUTF8) {
    	// Language
        byte[] langBytes = locale.getLanguage().getBytes(CHARSET_US_ASCII);

        // Text
		Charset utfEncoding = encodeInUTF8 ? 
				CHARSET_UTF_8 : CHARSET_UTF_16;
        byte[] textBytes = text.getBytes(utfEncoding);

        // Encoding
        int utfBit = encodeInUTF8 ? 0 : (1 << 7);
        char status = (char) (utfBit + langBytes.length);

        // Build NDEF record
        byte[] data = new byte[1 + langBytes.length + textBytes.length]; 
        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
    }

	@Override
	public View getView(Activity activity, LayoutInflater inflater,
			ViewGroup parent, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		return new String(NdefRecord.RTD_TEXT);
	}
}
