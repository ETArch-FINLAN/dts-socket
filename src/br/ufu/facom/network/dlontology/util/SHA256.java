package br.ufu.facom.network.dlontology.util;

import java.security.MessageDigest;

public class SHA256 {

	public static String getHash(String string) {

		try {

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(string.getBytes());
			byte[] mdbytes = md.digest();
			StringBuffer sb = new StringBuffer();
			
			for (int i = 0; i < 12; i++) {
				sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			
			return sb.toString();

		} catch (Exception e) {
			
			return null;
			
		}
	}

}
