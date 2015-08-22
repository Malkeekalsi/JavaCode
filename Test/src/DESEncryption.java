package bandhan.password.encryption;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class DESEncryption {

	private static final String UNICODE_FORMAT = "UTF8";
	public static final String DES_ENCRYPTION_SCHEME = "DES";
	private KeySpec myKeySpec;
	private SecretKeyFactory mySecretKeyFactory;
	private Cipher cipher;
	byte[] keyAsBytes;
	private String myEncryptionKey;
	private String myEncryptionScheme;
	SecretKey key;

	public DESEncryption() throws Exception
	{
		myEncryptionKey = "ThisIsSecretEncryptionKey";
		myEncryptionScheme = DES_ENCRYPTION_SCHEME;
		keyAsBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
		myKeySpec = new DESKeySpec(keyAsBytes);
		mySecretKeyFactory = SecretKeyFactory.getInstance(myEncryptionScheme);
		cipher = Cipher.getInstance(myEncryptionScheme);
		key = mySecretKeyFactory.generateSecret(myKeySpec);
	}

	/**
	 * Method To Encrypt The String
	 */
	public String encrypt(String unencryptedString) {
		String encryptedString = null;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
			byte[] encryptedText = cipher.doFinal(plainText);
			BASE64Encoder base64encoder = new BASE64Encoder();
			encryptedString = base64encoder.encode(encryptedText);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return encryptedString;
	}
	/**
	 * Method To Decrypt An Ecrypted String
	 */
	public String decrypt(String encryptedString) {
		String decryptedText=null;
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);
			BASE64Decoder base64decoder = new BASE64Decoder();
			byte[] encryptedText = base64decoder.decodeBuffer(encryptedString);
			byte[] plainText = cipher.doFinal(encryptedText);
			decryptedText= bytes2String(plainText);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return decryptedText;
	}
	/**
	 * Returns String From An Array Of Bytes
	 */
	private static String bytes2String(byte[] bytes) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			stringBuffer.append((char) bytes[i]);
		}
		return stringBuffer.toString();
	}

	public static void main(String[] args) 
	  {
		  String sEncrypt = "";
		  String sDecrypt = "";
		  String sPassword="";
		  try
		  {
			DESEncryption myEncryptor= new DESEncryption();	
			StringBuffer sb = new StringBuffer();
			String line = null; 
			BufferedReader br = null; 
			BufferedWriter bw = null;
			String sKey="";
			String sValue="";
			
				
					sKey	= "Malkeet";
					sValue	= "Password";
					System.out.println("sKey:"+sKey+"\tsValue::"+sValue);
					sDecrypt = myEncryptor.decrypt(sValue);
					//System.out.println("sDecrypt:"+sDecrypt);
					if(sDecrypt!=null && sDecrypt.startsWith("NSTL"))
					 {
						 //System.out.println("Decrypted "+sDecrypt.replaceAll("NSTL",""));
						 //sValue = sDecrypt.replaceAll("NSTL","");
						 sPassword= sDecrypt.replaceAll("NSTL","");
					 }
					else
					{
						sPassword = sValue;
						//System.out.println("Incrypted "+myEncryptor.encrypt("NSTL"+sValue));
						sValue = myEncryptor.encrypt("NSTL"+sValue);
					}
					System.out.println("sPassword : "+sValue);
			
		  }catch(Exception exp)
		  {
			  System.out.println("Error");
		  }
	  }
}

