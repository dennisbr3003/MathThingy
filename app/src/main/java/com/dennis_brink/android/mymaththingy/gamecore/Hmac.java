package com.dennis_brink.android.mymaththingy.gamecore;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Hmac {
	
	private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
	
	public static String generateHmacSha256 (String key, String data) throws Exception {
		
		try {
			return generateHmac(key, data, false);
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			throw e;
		}
	}
	
	public static String generateHmacSha256 (String key, String data, Boolean Base64urlEncode) throws Exception {
		
		try {
			return generateHmac(key, data, Base64urlEncode);
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			throw e;
		}
		
	}
	
	private static String generateHmac(String key, String data, Boolean Base64urlEncode)  throws NoSuchAlgorithmException, InvalidKeyException {
		
		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(),  "HmacSHA256");
	    Mac mac = Mac.getInstance("HmacSHA256");
	    mac.init(secretKeySpec);
	    String hexcode = bytesToHex(mac.doFinal(data.getBytes())); 
	    if(Base64urlEncode) {
	    	return Base64.getUrlEncoder().withoutPadding().encodeToString(hexcode.getBytes());
	    } else return hexcode;
	}
	
	private static String bytesToHex(byte[] bytes) {	
		
	    char[] hexChars = new char[bytes.length * 2];
	    for (int j = 0; j < bytes.length; j++) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }	    
	    return new String(hexChars);
	}
	
	
}
