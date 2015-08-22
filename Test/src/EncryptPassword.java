package newgen.bandhan.encryption;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Scanner;


public class EncryptPassword {

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
			System.out.println("********************Enter String to Encrypt***************");
			Scanner input =new Scanner(System.in);
			sKey =input.next().toString();
			//System.out.println("Incrypted "+myEncryptor.encrypt("NSTL"+sValue));
			sValue = myEncryptor.encrypt("NSTL"+sValue);
			System.out.println("Encrypted Value is ---->>>> \n\n"+sValue+"\n");
			
		  }catch(Exception exp)
		  {
			  exp.printStackTrace();
			  System.out.println("Error");
		  }
	  }

}
