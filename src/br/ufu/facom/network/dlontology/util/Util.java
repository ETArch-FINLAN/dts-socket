package br.ufu.facom.network.dlontology.util;

import java.net.NetworkInterface;
import java.net.SocketException;

public class Util {
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static byte[] getAddrMacByInterfaceName(String ifname){
		
		NetworkInterface net;
		try {
			net = NetworkInterface.getByName(ifname);
			return net.getHardwareAddress();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	public static byte[] concat(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		
		return c;
	}
	
}
