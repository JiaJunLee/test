package com.encrypt.system.algorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.encrypt.system.view.MainFrame.FileReadListener;

public class FileEncryptFactory {
	
	public static final String AES_ALGORITHM = "AES";
	public static final String DES_ALGORITHM = "DES";
	public static final String DESede_ALGORITHM = "DESede";
	
	private String algorithm;
	private String metaKey;
	private SecretKey keySpec;
	private Cipher encodeCipher;
	private Cipher decodeCipher;
	private FileReadListener fileReadListener;
	
	public FileEncryptFactory(String algorithm, String metaKey, FileReadListener fileReadListener){
		this.algorithm = algorithm;
		this.metaKey = metaKey;
		this.fileReadListener = fileReadListener;
		
		initKey();
		initCiphers();
	}
	
	public void decrypt(File destFile, File file) throws Exception {
		InputStream is = new FileInputStream(destFile);
        OutputStream out = new FileOutputStream(file);
 
        CipherInputStream cis = new CipherInputStream(is, decodeCipher);
        byte[] buffer = new byte[1024];
        int r;
        long readLength = 0;
        while ((r = cis.read(buffer)) > 0) {
            out.write(buffer, 0, r);
            readLength += r;
            fileReadListener.onLoad(readLength);
        }
        cis.close();
        is.close();
        out.close();
    }

	private void initCiphers() {
		try {
			
			encodeCipher = Cipher.getInstance(algorithm);
			encodeCipher.init(Cipher.ENCRYPT_MODE, keySpec);
			
			decodeCipher = Cipher.getInstance(algorithm);
			decodeCipher.init(Cipher.DECRYPT_MODE, keySpec);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}
	
	public void encrypt(File file, File destFile) throws Exception {
        InputStream is = new FileInputStream(file);
        OutputStream out = new FileOutputStream(destFile);
 
        CipherInputStream cis = new CipherInputStream(is, encodeCipher);
        byte[] buffer = new byte[1024];
        int r;
        long readLength = 0;
        while ((r = cis.read(buffer)) > 0) {
            out.write(buffer, 0, r);
            readLength += r;
            fileReadListener.onLoad(readLength);
        }
        cis.close();
        is.close();
        out.close();
    }

	private void initKey() {
		String tempKey = Base64.encode(metaKey.getBytes());
		switch(algorithm){
		case DESede_ALGORITHM:
			tempKey = tempKey.substring(0, 12);
			tempKey += tempKey;
			break;
		case AES_ALGORITHM:
			tempKey = tempKey.substring(0, 8);
			tempKey += tempKey;
			break;
		case DES_ALGORITHM:
			tempKey = tempKey.substring(0, 8);
			break;
		}
		
		keySpec = new SecretKeySpec(tempKey.getBytes(), algorithm);
		System.out.println(keySpec.getEncoded().length);
		System.out.println(Base64.encode(keySpec.getEncoded()));

	}

}
