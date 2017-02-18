package com.encrypt.system.algorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sun.corba.se.impl.ior.ByteBuffer;

public class EncryptFactory {
	
	public static final String MD5_ALGORITHM = "MD5";
	public static final String SHA_1_ALGORITHM = "SHA-1";
	public static final String SHA_224_ALGORITHM = "SHA-224";
	public static final String SHA_256_ALGORITHM = "SHA-256";
	public static final String SHA_384_ALGORITHM = "SHA-384";
	public static final String SHA_512_ALGORITHM = "SHA-512";
	
	private static HashMap<String, Integer> algorithms = new HashMap<String, Integer>();
	static{
		algorithms.put(MD5_ALGORITHM, 0);
		algorithms.put(SHA_1_ALGORITHM, 1);
		algorithms.put(SHA_224_ALGORITHM, 2);
		algorithms.put(SHA_256_ALGORITHM, 3);
		algorithms.put(SHA_384_ALGORITHM, 4);
		algorithms.put(SHA_512_ALGORITHM, 5);
	}

	private static MessageDigest[] messageDigests = new MessageDigest[6];

	private EncryptFactory() {
		try {
			Iterator iter = algorithms.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				messageDigests[(Integer)entry.getValue()] = MessageDigest.getInstance((String)entry.getKey());
			}
			for(int i=0;i<messageDigests.length;i++){
				
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static EncryptFactory getInstance() {
		return new EncryptFactory();
	}
	
	public void update(byte[] input, int offset, int len, String algorithm){
		int temp = algorithms.get(algorithm);
		messageDigests[temp].update(input, offset, len);
	}
	
	public byte[] toHashBytes(byte[] input, String algorithm){
		int temp = algorithms.get(algorithm);
		messageDigests[temp].update(input);
		return messageDigests[temp].digest();
	}

	public byte[] toHashBytes(String algorithm) {
		int temp = algorithms.get(algorithm);
		return messageDigests[temp].digest();
	}

	public static String toHexString(byte data[]) throws UnsupportedEncodingException {
		StringBuffer strBuffer = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			String hex = Integer.toHexString(0xff & data[i]);
			if (hex.length() == 1) {
				strBuffer.append('0');
			}
			strBuffer.append(hex);
		}
		return strBuffer.toString();
	}
	

}
