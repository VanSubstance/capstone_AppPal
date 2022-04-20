package com.appPal.main.util;

import java.security.MessageDigest;
import java.util.UUID;

public class UtilFunction {
	
	public static String generateUuidV4() {
		return UUID.randomUUID().toString();
	}

	public static String testSHA256(String password) {
		String result = null;
		try{

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(password.getBytes("UTF-8"));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			//출력
			result = hexString.toString();
			
		} catch(Exception ex){
			throw new RuntimeException(ex);
		}
		return result;
	}
}
