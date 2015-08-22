/*------------------------------------------------------------------------------------------------------
												NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group: Application -Projects

Project/Product:  Bandhan AO LOS Process

Application: Bulk Documnet Upload Utility

Module: gui package, main class

File Name: AutoServer.java

Author: Divya Gautam

Date (DD/MM/YYYY): 15/05/2015

Description: This is main class file that connects to server and processes workitems.

-------------------------------------------------------------------------------------------------------

CHANGE HISTORY

-------------------------------------------------------------------------------------------------------

Problem No/CR No   Change Date   Changed By    Change Description

------------------------------------------------------------------------------------------------------*/

package com.newgen.gui;
import java.io.*;
import java.util.*;
import javax.swing.*;
import com.incors.plaf.kunststoff.*;
import com.newgen.autoserver.*;
import com.newgen.autoserver.xml.XMLGen;
import com.newgen.autoserver.xml.XMLParser;
import com.newgen.autoserver.GlobalVar;
import com.newgen.wfdesktop.xmlapi.WFXmlResponse;
import com.newgen.wfdesktop.xmlapi.WFXmlList;
import com.newgen.wfdesktop.xmlapi.WFInputXml;


public class AutoServer {
	 
    XMLParser xmlParser = new XMLParser();	
    FunctionsWI objWF;
	ProcessData tem = null;
    Thread t;
	public AutoServer() {}			
    public AutoServer(FunctionsWI functionswi) {
    try 
	{
    
        objWF = functionswi;
        startFunction();
    } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String args[]) 
	{
    	System.out.println("Entry Main Function in Autoserver.java");
		FunctionsWI functionswi = new FunctionsWI();

        AutoServer obj = new AutoServer(functionswi);
    	System.out.println("Exit Main Function in Autoserver.java");
    }

    public void startFunction() {

        String strxml = readConfig();
    	
        //System.out.println("strxml "+strxml);

		String strSMSpassword="";
        if (!strxml.equals("")) {
            xmlParser.setInputXML(strxml);
			String ServerType = xmlParser.getValueOf("ServerType").trim();
            String strServerIP = xmlParser.getValueOf("JTSIP").trim();
            String strPort = xmlParser.getValueOf("JTSPORT").trim();
            String strCabinetName = xmlParser.getValueOf("CabinetName").trim();
            String strLogPath = xmlParser.getValueOf("LogPath").trim();
            String strPollInterval = xmlParser.getValueOf("pollingIntelval").trim();
            String strAccountTable = xmlParser.getValueOf("ACCOUNTTABLE").trim();
            String strProcessName = xmlParser.getValueOf("ProcessName").trim();
            String strProcessDefID = xmlParser.getValueOf("ProcessDefID").trim();
            String strVolId = xmlParser.getValueOf("VolumeID").trim();
           	String strQueryBatchSize = xmlParser.getValueOf("QueryBatchSize").trim();
            String strLogSize = xmlParser.getValueOf("LogSize").trim();
            String strSleepTime = xmlParser.getValueOf("SleepTime").trim();
            String sActivityId = xmlParser.getValueOf("ActivityID").trim();
            String sActivityId2 = xmlParser.getValueOf("ActivityID2").trim();
			String sActivityName = xmlParser.getValueOf("ActivityName").trim();
			String sActivityName2 = xmlParser.getValueOf("ActivityName2").trim();
            String sUploadFromPath = xmlParser.getValueOf("UploadFrom").trim();
			String sSuccFolderPath = xmlParser.getValueOf("SucessFolder").trim();
			String sFailurePath = xmlParser.getValueOf("FailedFolder").trim();
			String sDocTypesMultiple = xmlParser.getValueOf("DocTypesMultiple").trim();
			String sDocTypesSingle = xmlParser.getValueOf("DocTypesSingle").trim();
			String sFileType = xmlParser.getValueOf("FileType").trim();
			String sUsrName = xmlParser.getValueOf("USERNAME").trim();
			String sPass = xmlParser.getValueOf("PASSWORD").trim();
			String sPIDColumn = xmlParser.getValueOf("WIName_Column").trim();
			String sFileMatchParam = xmlParser.getValueOf("filematchparam").trim();
			String sCurWKCol = xmlParser.getValueOf("CrrentWorkstepColumn").trim();
			String sAutoCompFlag = xmlParser.getValueOf("autocompleteflag").trim();
            
            if (ServerType.equals("") || strServerIP.equals("") || strPort.equals("") ||
                strCabinetName.equals("") || 
                strLogPath.equals("") ||strPollInterval.equals("") || strAccountTable.equals("") ||
				 strProcessName.equals("") || strProcessDefID.equals("") || strVolId.equals("") || strQueryBatchSize.equals("") || 
				strLogSize.equals("") 
				|| strSleepTime.equals("") || sActivityId.equals("") || sActivityId2.equals("") || sActivityName.equals("") || sActivityName2.equals("") || sUploadFromPath.equals("") || sSuccFolderPath.equals("") || sFailurePath.equals("") || sDocTypesMultiple.equals("") ||  sDocTypesSingle.equals("") || sFileType.equals("") ||  sUsrName.equals("") || sPass.equals("")  || sPIDColumn.equals("") || sFileMatchParam.equals("") || sCurWKCol.equals("") || sAutoCompFlag.equals("") ) 
			{
            	System.out.println("Incomplete information in configuration file.");
                Log.generateLog("Incomplete information in configuration file.");
            } 
			else if (isValidIp(strServerIP) == false) 
			{
                Log.generateLog("Invalid Server IP  in " +GlobalVar.titleName + " configuration file.");
            } else if (isValidPort(strPort) == false) {
                Log.generateLog("Invalid Port in " +
                              GlobalVar.titleName +
                              " configuration file.");
                System.out.println("Invalid Port in " +GlobalVar.titleName +" configuration file.");
            } else if (isDirectory(strLogPath) == false) {
                Log.generateLog("Invalid Log Directory in " +
                              GlobalVar.titleName +
                              " configuration file.");
                System.out.println("Invalid Log Directory in " +
                              GlobalVar.titleName +
                              " configuration file.");
            }
			else if (isInteger(strPollInterval) == false) {
                Log.generateLog("Invalid value of Polling Interval in " +
                              GlobalVar.titleName +
                              " configuration file.");
                System.out.println("Invalid value of Polling Interval in " +
                              GlobalVar.titleName +
                              " configuration file.");
			}
			else if (isDirectory(sUploadFromPath) == false) {
                Log.generateLog("Invalid Upload from Path Directory in " +
                              GlobalVar.titleName +
                              " configuration file.");
                System.out.println("Invalid Upload from Path Directory in " +
                              GlobalVar.titleName +
                              " configuration file.");
            }
			else if (isDirectory(sSuccFolderPath) == false) {
                Log.generateLog("Invalid Success Path Directory in " +
                              GlobalVar.titleName +
                              " configuration file.");
                System.out.println("Invalid Success Path Directory in " +
                              GlobalVar.titleName +
                              " configuration file.");
            }
			else if (isDirectory(sFailurePath) == false) {
                Log.generateLog("Invalid Failure Path Directory in " +
                              GlobalVar.titleName +
                              " configuration file.");
                System.out.println("Invalid Failure Path Directory in " +
                              GlobalVar.titleName +
                              " configuration file.");
            }
			else {
				objWF.ServerType = ServerType;
                objWF.serverIP = strServerIP;
                objWF.serverPort = Integer.parseInt(strPort);
                objWF.cabinetName = strCabinetName;
				GlobalVar.logPath = strLogPath;
                GlobalVar.pollInterval = Integer.parseInt(strPollInterval);
				objWF.AccountTable=strAccountTable;
				objWF.ProcessName=strProcessName;
				objWF.processDefID=strProcessDefID;
				objWF.VolumeID = strVolId;
				objWF.QueryBatchSize=strQueryBatchSize;
				GlobalVar.strLogSize=Long.parseLong(strLogSize);
				objWF.SleepTime=strSleepTime;
				objWF.activityID = sActivityId;
				objWF.activityID2 = sActivityId2;
				objWF.activityName = sActivityName;
				objWF.activityName2 = sActivityName2;
				objWF.uploadFrom=sUploadFromPath;
				objWF.successPath=sSuccFolderPath;
				objWF.failurePath=sFailurePath;
				String[] docTypesList = sDocTypesMultiple.split(",");
				for(int i=0; i<docTypesList.length; i++) {
					objWF.docTypeMultiple.add(docTypesList[i]);
				}
				docTypesList = sDocTypesSingle.split(",");
				for(int i=0; i<docTypesList.length; i++) {
					objWF.docTypeSingle.add(docTypesList[i]);
				}
				
				objWF.fileType = sFileType;
				
				objWF.userName = sUsrName;
        		objWF.password = sPass;
				objWF.pidColumn = sPIDColumn;
				objWF.fileMatchParam = sFileMatchParam;
				objWF.autoCompFlag = sAutoCompFlag;
				objWF.currWKColm = sCurWKCol;

                Log.setLogDir();
        		//System.out.println("Outside FetchSSLPassword Function in Autoserver.java");
				//if(objWF.FetchSSLPassword())
        		
				{
					//System.out.println("Inside FetchSSLPassword Function in Autoserver.java");
					try {
					
						System.out.println("Before Calling connectToServer Function in Autoserver.java");
						boolean flag = objWF.connectToServer();
						//boolean flag = true;
						System.out.println("After Calling connectToServer Function in Autoserver.java");
						if (flag) 
						{
							System.out.println("Before Calling connectToWorkFlow Function in Autoserver.java");
							String functionOut = objWF.connectToWorkFlow("Y");
							String desc = getErrorName(functionOut);
							int i = getErrorCode(functionOut);
							if (i == 0) 
							{
								startServer();
							} 
							else if (i == -50167) 
							{
								int j = 0;
								if (j == 0) 
								{
									String functionOut1 = objWF.connectToWorkFlow("N");
									int k = getErrorCode(functionOut1);
									desc = getErrorName(functionOut); ;
									if (k == 0) 
									{
										startServer();
									}
								}
							} 
							else 
							{
								if (desc.equals("")||desc.equals("null"))
								{
									desc="Application Server Down";
								}
								Log.generateLog(desc);
							}
							try 
							{
								//objWF.disconnectFromWorkFlow();
								//objWF.disconnectFromServer();
							} catch (Exception e) {}

						} else {
							if(objWF.Err.equals("java.net.ConnectException: Connection refused: connect"))
							{
								Log.generateLog(
										"WorkFlow Server Down (Socket Error).");								
							}
							else
							{
								Log.generateLog(
										"Invalid Workflow Server IP and Port are registered.");
							}

							System.exit(0);
						}
					} catch (Exception exception1) {
						Log.generateLog("Error in Login : " +
									  exception1.toString());

						System.exit(0);
					}
				}
            }
        }
		else
		{
			Log.generateLog("Blank xml file.");
		}
    }
	private void startServer() 
	{
		/*if(objWF.setQueue())
		{
			Log.generateLog("[AutoServer.startFunction] Calling ServerWindow");
			*/
			cmdStart();
		/*}
		else
		{
			Log.generateLog("User have no rights on Queue.");
		}*/
	}
	private void cmdStart()
	{
		try 
		{
			if(t==null)
			{
				t = new Thread(tem);
				tem = new ProcessData(objWF);
				t = new Thread(tem);
				t.setPriority(3);
				Log.generateLog("New Thread Created");
			}
			t.start();
		} catch (Exception exp) {
				Log.generateLog(exp.toString());
		}
	}

	public static String readConfig() {

        String str = "";
        //System.out.println(System.getProperty("user.dir")+"---"+ GlobalVar.sortTitleName);
        try {
        	FileReader fr = new FileReader(System.getProperty("user.dir") +
                                           File.separator +GlobalVar.sortTitleName+
                                           ".XML");
            BufferedReader br = new BufferedReader(fr);
            String Record = "";
            while ((Record = br.readLine()) != null) {
                str = str + Record;
            }
            fr.close();
        } catch (FileNotFoundException e) {
           /* Log.generateLog("Please register " +
                          GlobalVar.titleName +
                          " configuration.");  */
        	System.out.println(e.toString());
			Log.generateLog("File " + GlobalVar.sortTitleName +
                          " not found.");
//            System.exit(0);
        } catch (Exception e) {
            Log.generateLog(GlobalVar.titleName + e.toString());

        }
		//Log.generateLog("str"+str);
        return str;
    }

    public static boolean isBoolean(String s) {
        try {
            if (s.equals("")) {
                return false;
            }
            if (Boolean.getBoolean(s)) {
                return true;
            }
            if (!Boolean.getBoolean(s)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean isInteger(String s) {
        try {
            if (s.equals("")) {
                return false;
            }
            Integer.parseInt(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean getBoolean(String s) {
        if (s.equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    public int getErrorCode(String str) {
        String temp[] = str.split("~");
        if (isInteger(temp[0])) {
            return Integer.parseInt(temp[0]);
        }
        return -9;
    }

    public String getErrorName(String str) {
        String temp[] = str.split("~");
        return temp[1];
    }
	public static boolean isValidIp(String s) {
        try {
            String parts[] = s.split("\\.");
            if (parts.length != 4) {
                return false;
            }
            for (int i = 0; i < parts.length; i++) {
                int ip = Integer.parseInt(parts[i]);
                if (ip < 0 || ip > 255) {
                    return false;
                }
            }
        } catch (Exception e) {
            Log.generateLog(e.toString());
            return false;
        }
        return true;
    }

    public static boolean isValidPort(String s) {
        try {
            if (Integer.parseInt(s) <= 0) {
                return false;
            }
        } catch (Exception e) {
            Log.generateLog(e.toString());
            return false;
        }
        return true;
    }

	public static boolean isDirectory(String s) {
        try {
            File directory = new File(s);
            if (!directory.exists()) {
                return false;
            }
        } catch (Exception e) {
            Log.generateLog(e.toString());
            return false;
        }
        return true;
    }
}
