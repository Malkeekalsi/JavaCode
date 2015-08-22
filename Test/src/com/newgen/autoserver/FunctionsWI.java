/*------------------------------------------------------------------------------------------------------
												NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group: Application -Projects

Project/Product:  Bandhan AO LOS Process

Application: Bulk Documnet Upload Utility

Module: autoserver package

File Name: FunctionsWI.java

Author: Divya Gautam

Date (DD/MM/YYYY): 15/05/2015

Description: This class contains some basic methods used by AutoServer.java and ProcesData.java

-------------------------------------------------------------------------------------------------------

CHANGE HISTORY

-------------------------------------------------------------------------------------------------------

Problem No/CR No   Change Date   Changed By    Change Description

------------------------------------------------------------------------------------------------------*/
package com.newgen.autoserver;


import java.io.*;
import java.util.*;

import com.newgen.autoserver.*;
import com.newgen.autoserver.xml.*;
//import com.newgen.gui.*;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.wfdesktop.xmlapi.*;
import com.newgen.omni.wf.util.excp.*;
import com.newgen.omni.jts.client.*;

public class FunctionsWI {

    XMLParser xmlParser = new XMLParser();
	public String ServerType= "";
    public String serverIP = "";
    public int serverPort = 0;
    public String cabinetName = "";
	
    public String sessionID = "";
	public String userID="";
	public String smsLoginID="";
	public String smsPassword="";
	
	public String AccountTable="";
	public String ProcessName="";
	public String processDefID="";
	public String VolumeID="";
	public String QueryBatchSize="50";
	public String SleepTime="5";
	public String activityID = "";
	public String activityID2 = "";
	public String activityName = "";
	public String activityName2 = "";
	
	public String uploadFrom = "";
	public String successPath = "";
	public String failurePath = "";
	public String userName = "";
    public String password = "";
	public String pidColumn = "";
	public String fileMatchParam = "";
	public String autoCompFlag = "";
	public String currWKColm = "";
	public String batchSize= "250";
	public String Err="";
	public String strSessionId= "";
	public ArrayList docTypeMultiple = new ArrayList();
	public ArrayList docTypeSingle = new ArrayList();
	public String fileType = "";
	
	NGEjbClient ngEjbClient;
	WFCallBroker wfCallBroker;
	
	
   public boolean connectToServer() {
	   
       try{
    	   //this.cabinetName = "testingorc";
    	   //this.userName = "supervisor2";
    	   //this.password = "supervisor351";
		   
   String ipXml = "<?xml version=1.0?><NGOConnectCabinet_Input><Option>NGOConnectCabinet</Option><CabinetName>" 
    			   +
    			               this.cabinetName + "</CabinetName>" +
    			               "<UserName>" + this.userName + "</UserName>" +
    			               "<UserPassword>" + this.password + "</UserPassword>" +
    			               "<UserExist>N</UserExist>" +
    			               "<ListSysFolder>N</ListSysFolder>" +
    			               "<UserType>U</UserType>" +
    			               "</NGOConnectCabinet_Input>";
   						
   
   NGEjbClient objClient = null;
   objClient = NGEjbClient.getSharedInstance();
   //String strXmlout = objClient.makeCall(this.serverIP,"4447", this.ServerType, ipXml.toString());
    			        String strXmlout = wfCallBroker.execute(ipXml,String.valueOf(this.serverIP) , this.serverPort, 0);		
							
    			             WFXmlResponse xmlResponse = new WFXmlResponse(strXmlout);
    			             if (xmlResponse.getVal("Status").equals("0"))
    			             {
    			               System.out.println("Connected To OD");
    			              //this.strSessionId = xmlResponse.getVal("UserDBId");
    			               //String prevSessionId = xmlResponse.getVal("UserDBId");
    			             }
    			             else
    			             {
    			              System.out.println(strXmlout);
    			             }
		  
       ngEjbClient = NGEjbClient.getSharedInstance();
         ngEjbClient.initialize(this.serverIP ,String.valueOf(this.serverPort) ,ServerType);
			System.out.println("Connected To Server in FunctionWI.java");
           Log.generateLog("Connected To Server");
       //    return true;
       }catch(NGException ngE){
           Log.generateLog("7:"+ngE.toString());
           ngE.printStackTrace();
          // return false;
       } catch (IOException e) {
		e.printStackTrace();
	} catch (Exception e) {
		e.printStackTrace();
	} 
	   
	   return true;
   }
   public void disconnectFromServer() {
       try {
           //ngEjbClient=null;
    	   wfCallBroker=null;
           System.out.println("DisConnected From Server in FunctionWI.java");
		   Log.generateLog("DisConnected From Server");
		    //ngEjbClientOD=null;

       } catch (Exception e) {
           Log.generateLog("Exception in disconnectFromServer(): "+e.toString());
       }
   }

	public String execute(String inXml) 
	{
		String prevSessionId = this.sessionID;
		//System.out.println("In FunctionWI.java prevSessionIddddd is :-"+prevSessionId);
		Log.generateLog("In FunctionWI.java prevSessionIddddd is : "+prevSessionId);
		String outXml = "";
		try 
		{
			//System.out.println("B4 calling makeCall In FunctionWI.java");
			//outXml = ngEjbClient.makeCall(inXml);
			outXml =  wfCallBroker.execute(inXml,this.serverIP , this.serverPort, 0);
			//System.out.println(" After calling makeCall In FunctionWI.java");
            Log.generateLog(inXml);
            Log.generateLog(outXml);
		} 
		catch (NGException ngE) 
		{
			outXml="Invalid Session";
			Log.generateLog("Exception in execute(): "+ngE.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(outXml.toUpperCase().indexOf("INVALID SESSION")>-1)
		{
			try
			{
				Thread.sleep(10000);
			}
			catch(Exception ex)
			{
				Log.generateLog(ex.toString());
			}
			reconnectToWorkflow();
			if(inXml.toUpperCase().indexOf("<SESSIONID>")>-1)
				inXml = inXml.substring(0,inXml.indexOf("<SessionId>")+11) +this.sessionID+inXml.substring(inXml.indexOf("</SessionId>"),inXml.length());
			else if(inXml.toUpperCase().indexOf("<DBUSERID>")>-1)
				inXml = inXml.substring(0,inXml.indexOf("<DBUserId>")+11) +this.sessionID+inXml.substring(inXml.indexOf("</DBUserId>"),inXml.length());
			//System.out.println("===="+inXml);
			//inXml.replaceAll(">"+prevSessionId+"</",this.sessionID);
			try 
			{
				//outXml = ngEjbClient.makeCall(inXml);
				outXml =  wfCallBroker.execute(inXml,this.serverIP , this.serverPort, 0);
				if (GlobalVar.printScreenflag == true) 
				{
				   Log.generateLog(inXml);
				   Log.generateLog(outXml);
				}
				return outXml;
			} 
			catch (NGException ngE1) 
			{
				Log.generateLog("exec1 "+ngE1.toString());
			} 
			catch (Exception ex) 
			{
				Log.generateLog("exec2 "+ex.toString());
			}
			return "";
		}
		else
			return outXml;
	}

	private void reconnectToWorkflow() 
	{
        try 
		{
           // disconnectFromWorkFlow();
            disconnectFromServer();
        } 
		catch (Exception ex) 
		{
            Log.generateLog("1:"+ex.toString());
        }
        try 
		{
            if (connectToServer()) 
			{
                String str = connectToWorkFlow("N");
				String temp[] = str.split("~");
                if (!temp[0].equals("0")) 
				{
				    Thread.sleep(30000);
                    reconnectToWorkflow();
                }
            }
        } 
		catch (Exception ex) 
		{
            Log.generateLog("2:"+ex.toString());
        }
    }
	
   public String executeWithoutInLog(String inXml) {
       try {
           //System.out.println("...");
		   //String outXml = ngEjbClient.makeCall(inXml);
    	 String outXml =  wfCallBroker.execute(inXml,this.serverIP , this.serverPort, 0);
		   //System.out.println("outXml:"+outXml);
           if (GlobalVar.printScreenflag == true) {
               Log.generateLog(outXml);
           }
           return outXml;
       } catch (NGException ngE) {
           Log.generateLog("3:"+ngE.toString());
           disconnectFromServer();
           if (connectToServer()) {
               try {
                  // String outXml = ngEjbClient.makeCall(inXml);
            	   String  outXml =  wfCallBroker.execute(inXml,this.serverIP , this.serverPort, 0);
                   if (GlobalVar.printScreenflag == true) {
                       Log.generateLog(outXml);
                   }
                   return outXml;
               } catch (NGException ngE1) {
                   Log.generateLog("4:"+ngE1.toString());
               } catch (Exception ex) {
                   Log.generateLog("5:"+ex.toString());
               }
           }
           return "";
       } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return inXml;
   }


    public String connectToWorkFlow(String forceful) {
        int i = -9;
        String desc = null;
        String xmlInput = null;
        String xmlOutput = null;
        try {
        	//System.out.println("Inside connectToWorkFlow Function in FunctionWI.java");
            xmlInput = XMLGen.get_WMConnect_Input(this.
                                                  cabinetName, this.userName,
                                                  this.password, forceful);
			//System.out.println("xmlInput:"+xmlInput);
            xmlOutput = this.execute(xmlInput);
			//System.out.println("xmlOutput:"+xmlOutput);
            xmlParser.setInputXML(xmlOutput);
            String s9 = xmlParser.getValueOf("Option");
            if (!s9.equalsIgnoreCase("WMConnect")) {
                return "-9~Invalid Workflow Server IP and Port are registered.";
            }
            String s6 = xmlParser.getValueOf("MainCode");
            try
			{
				i = Integer.parseInt(s6);
			}
			catch(Exception ex)
			{
				s6 = xmlParser.getValueOf("Status");
				i = Integer.parseInt(s6);
			}
            if (i == 0) {
                this.sessionID = xmlParser.getValueOf("SessionID");
                this.userID = xmlParser.getValueOf("ID");

            } else {
                String s7 = xmlParser.getValueOf("SubErrorCode");
                desc = xmlParser.getValueOf("Description");
                i = Integer.parseInt(s7);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i + "~" + desc;
    }


    public void disconnectFromWorkFlow() {
        String str_inxml = XMLGen.get_WMDisConnect_Input(this.cabinetName,
                this.sessionID);
        String str_outxml = execute(str_inxml);
        //System.out.println("Inside disconnectFromWorkFlow Function in FunctionWI.java");
		Log.generateLog("DisConnected From OF Server");
    }

public String setQueue(String actID) 
{
	try 
	{
		
            String inputXML = XMLGen.get_WFGetUserQueueDetails_Input(this.cabinetName, this.sessionID, this.processDefID,actID, this.userID);
            String outputXML = this.execute(inputXML);
            xmlParser.setInputXML(outputXML);
			String maincode=xmlParser.getValueOf("MainCode").trim();
			if (maincode.equals("0")) 
			{
               String QueueId="";
				Log.generateLog("Checked :user have rights on this process "+this.processDefID+" and activity "+actID);
				String QueueName = xmlParser.getValueOf("QueueName").trim();
				inputXML = XMLGen.get_WFGetIdforName_Input(this.cabinetName,this.sessionID,this.processDefID,"Q", QueueName);
			
				outputXML = this.execute(inputXML);
				//logger.info("getQueueId outputXML "+outputXML);       
				xmlParser.setInputXML(outputXML);
				if(xmlParser.getValueOf("MainCode").equals("0")) {
					QueueId=xmlParser.getValueOf("QueueId");
				}
				return QueueId;               
            }
			else if(maincode.equals("11")){
				this.connectToWorkFlow("N");
				return setQueue(actID);
			} 
			//Added by Kapil To support deadlock bug in WFGetUserQueueDetails --03_04_2014 
			//Begin here
			else if(maincode.equals("15"))
			{
				Log.generateLog("WFGetUserQueueDetails_Input failed try Again!!");
				this.connectToWorkFlow("N");
				return setQueue(actID);
			}
			//End here
			else{
				Log.generateLog("Checked :user have no rights on this process "+this.processDefID+" and activity "+actID);
				Log.generateLog("setQueue inputXML "+inputXML);	
				Log.generateLog("setQueue outputXML "+outputXML);
				return null;
			}
    } 
	catch (Exception e) 
	{
		e.printStackTrace();
    }
    return null;
}   
	
    /*public boolean FetchSSLPassword() 
	{
        String strPassword = null;
		String strUserName = null;
        try {
        	System.out.println("Inside FetchSSLPassword Function in Function.java");
            File desFile = new File(sNPDATLocation);
            FileInputStream fis = null;
            FileOutputStream fos = null;
            CipherInputStream cis = null;

            // Read the cipher settings
            File KEY_FILE = new File(sKeyDataLocation);
            FileInputStream eksis = new FileInputStream(KEY_FILE);
            byte[] temp = new byte[(int) KEY_FILE.length()];
            int bytesRead = eksis.read(temp);
            //System.out.println("key1--"+new String(temp));
            byte[] encodedKeySpec = new byte[bytesRead];
            System.arraycopy(temp, 0, encodedKeySpec, 0, bytesRead);

            // Recreate the secret/symmetric key
            SecretKeySpec secretKey = new SecretKeySpec(encodedKeySpec, "DES");

            // Creation of Cipher objects
            Cipher decrypt =
                    Cipher.getInstance("DES/ECB/PKCS5Padding");
            decrypt.init(Cipher.DECRYPT_MODE, secretKey);

            // Open the Encrypted file
            fis = new FileInputStream(desFile);
            cis = new CipherInputStream(fis, decrypt);

            byte[] b = new byte[8];
            int i = cis.read(b);
            String sdata = null;
            while (i != -1) {
                sdata = sdata + (new String(b));
                i = cis.read(b);
            }
            cis.close();
            fis.close();
			int x = sdata.indexOf("<UserName>") + 10;
			int y = sdata.indexOf("</UserName>");
            int m = sdata.indexOf("<Password>") + 10;
            int n = sdata.indexOf("</Password>");
			//strUserName = sdata.substring(x, y);
            //strPassword = sdata.substring(m, n);
            strUserName = "bandhan2";
            strPassword = "test1234";
            System.out.println("UserName from file---"+strUserName);
            System.out.println("Password from file---"+strPassword);

        } catch (Exception e) {
            System.out.println("inside error--");
            e.printStackTrace();
            Log.generateLog("ERROR in fetching Encrypted UserName and Password ::--" + e.getMessage());
            return false;
        }

		this.userName = strUserName;
		this.password = strPassword;
        return true;
    }*/
}
