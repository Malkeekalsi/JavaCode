/*------------------------------------------------------------------------------------------------------
												NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group: Application -Projects

Project/Product:  Bandhan AO LOS Process

Application: Bulk Documnet Upload Utility

Module: autoserver package

File Name: ProcesData.java

Author: Divya Gautam

Date (DD/MM/YYYY): 15/05/2015

Description: The code here searches workitem based uplon appliction number and attaches/replaces the documents to workitem.

-------------------------------------------------------------------------------------------------------

CHANGE HISTORY

-------------------------------------------------------------------------------------------------------

Problem No/CR No   Change Date   Changed By    Change Description

------------------------------------------------------------------------------------------------------*/
package com.newgen.autoserver;

import java.io.*;

import com.newgen.niplj.fileformat.Tif6;
import com.newgen.wfdesktop.xmlapi.*;
import com.newgen.autoserver.xml.XMLGen;

import java.util.*;

import com.newgen.autoserver.GlobalVar;
import com.newgen.autoserver.xml.XMLParser;



//import com.newgen.omni.wf.util.xml.XMLParser;
import ISPack.CPISDocumentTxn;
import ISPack.ISUtil.JPDBRecoverDocData;
import ISPack.ISUtil.JPISException;
import ISPack.ISUtil.JPISIsIndex;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessData implements Runnable {
    WFXmlResponse InstrumentListXmlResponse = new WFXmlResponse();
    WFXmlList instrumentList = new WFXmlList();
    //WFXmlList SMSdataList = new WFXmlList();
    //WFXmlList dataList = new WFXmlList();
	WFXmlList LOSDataList = new WFXmlList();
	XMLParser xmlParser = new XMLParser();
    FunctionsWI objWF;
	static String strLastProcessInstance = "";
	static int iCount=0;
    Runtime r = Runtime.getRuntime();
    private boolean keepRunning = true;
    private static int processedWorkitem = 0;
	private static String DocIDX="";
    public int dataCount = 0;
	boolean bNextBatch=false;
	int iBatch=1;
	boolean locked=false;
	String strLOSReferenceNo="";
	boolean bConnected = false;
	String BranchName = "";
	String BranchAlpha = "";
	public String strLastWIName = "";
	private static short volIdShort;	
	//wfcustom_ejb wfcustom_ejb;
    public ProcessData(FunctionsWI functionswi) {
        objWF = functionswi;
    }

    public void stop() {
        keepRunning = false;
		processedWorkitem=0;
		iCount=0;

    }

    public void run() 
	{
        keepRunning=true;
		Log.generateLog(GlobalVar.titleName + " Started.\n");
        
		while (keepRunning) 
		{
            r.gc(); //garbage collector
            Log.generateLog("Start of Thread");
            try 
			{
				reconnectToWorkflow();
                int iRet = this.processWI();
                if (iRet == 11) 
				{
					Thread.sleep(10000);
                    reconnectToWorkflow();
					this.processWI();
                }
                if (keepRunning) 
				{
					objWF.disconnectFromWorkFlow();
					bConnected=false;
					waiteloop(GlobalVar.pollInterval * 1000);
                }
            }catch (Exception ex) {
					Log.generateLog(StackTraceToString(ex));
            }
			
            Log.generateLog("End of Thread");
        }
        Log.generateLog("\n" + GlobalVar.titleName + " Stopped.");
    }
    

   
	private int processWI() 
	{
			try{		
			Log.generateLog("Inside processWI():: for ActivityID  "+objWF.activityID+"  and "+objWF.activityID2+" UploadFrom  "+objWF.uploadFrom+"  For DocType With multiple instances "+objWF.docTypeMultiple+" and For DocType With single instance "+objWF.docTypeSingle);
			File folder = new File(objWF.uploadFrom);
			r.gc(); // garbage collector						
			//Log.generateLog("Inside processWI()");	
			String mainCode="";				
			String WItemID="1";
			String Query="";					
			String Queue_ID=objWF.setQueue(objWF.activityID);					
			String Queue_ID2=objWF.setQueue(objWF.activityID2);					
			if(Queue_ID==null || Queue_ID2 == null){
				System.out.println("User has no rights on process::"+Thread.currentThread().getName());
				System.out.println("Thread Name:: "+Thread.currentThread().getName()+" is going to stop,Please Give rights to user and restart the utility again.");
				Log.generateLog("\n User has no rights:::");	
				Thread.currentThread().stop();
				return -1;
			}else{
				Log.generateLog("\nCalling FecthWorkItems Function!!!!\n");				
				if(!folder.exists()){
					System.out.println("Location-->>"+objWF.uploadFrom+" Does not exists for or access is not possible for "+Thread.currentThread().getName());
					System.out.println("Thread Name:: "+Thread.currentThread().getName()+" is going to stop,Please check folder location.");	
					Thread.currentThread().stop();				
				}else{
					//Log.generateLog("Folder location"+struploadfrom+"Found.");
					boolean status_fetch=FecthWorkItems(Queue_ID,Queue_ID2,folder);
					if(!status_fetch){
							Log.generateLog("Error found calling FecthWorkItems");				
					}
				}
			}				
		}catch (RuntimeException e){		
			Log.generateLog(StackTraceToString(e));
		}	
		return 0;
	
	

	}
	
	
	private boolean FecthWorkItems(String ActivityId,String ActivityId2,File folder) {
		//String []strWorkItem=new String[0];
		String inXml="";
		String outXml="";
		String temp="";
		try {
			File[] listOfFolders =folder.listFiles();
			for (int i =0; i < listOfFolders.length; i++) {
				String appNum = listOfFolders[i].getName();
				String pattern = "^\\d{10}$";
				Pattern r = Pattern.compile(pattern);
				Matcher m = r.matcher(appNum);
				if (m.matches()) {
					Log.generateLog("Valid application number "+appNum);
					String strSelectQuery = "SELECT "+objWF.pidColumn+","+objWF.fileMatchParam+","+objWF.currWKColm+" FROM "+objWF.AccountTable +" WHERE "+objWF.fileMatchParam+" = '"+appNum+"' AND ("+objWF.currWKColm+" = '"+objWF.activityName+"' OR "+objWF.currWKColm+" = '"+objWF.activityName2+"')";
					inXml = XMLGen.APSelectWithColumnNames(objWF.cabinetName,objWF.sessionID,strSelectQuery);
					outXml = objWF.execute(inXml);	    	
					xmlParser.setInputXML(outXml);
					if (xmlParser.getValueOf("MainCode").equals("0")){
						//Log.generateLog("FecthWorkItems inXml:\n"+inXml);
						//Log.generateLog("FecthWorkItems outXml:\n"+outXml);
						int iTotalRetrievedCount1 = Integer.parseInt(xmlParser.getValueOf("TotalRetrieved"));
						if (iTotalRetrievedCount1 > 0){
							String WIName = xmlParser.getValueOf(objWF.pidColumn);
							String curWS = xmlParser.getValueOf(objWF.currWKColm);
							if(objWF.activityName.equals(curWS)) {
								temp=Document_Attach(folder,WIName,appNum);
							}
							else if(objWF.activityName2.equals(curWS)) {
								temp = Document_Replace(folder,WIName,appNum);
							}
						}
						else {
							Log.generateLog("No workitem with application number "+appNum+" present on "+objWF.activityName+" or "+objWF.activityName2+" workstep");
							System.out.println("No workitem with application number "+appNum+" present on "+objWF.activityName+" or "+objWF.activityName2+" workstep");
						}
						//return true;
					}
					else
					{
						Log.generateLog("APSELECT failed for application number "+appNum);
					}
				}
				else {
					Log.generateLog("Invalid application number "+appNum);
					System.out.println("Invalid application number "+appNum);
				}
					
			} 			
		}catch(Exception ex){
			System.out.println(StackTraceToString(ex));
			Log.generateLog("Document_Attach: "+StackTraceToString(ex));
			//strWorkItem= null;
			return false;
		}
		//Log.generateLog("strWorkItem-->>"+strWorkItem);
	return true;
	} 
	
	
	
	private String Document_Attach(File folder,String winame,String sAppNo) {
		try {		
			Log.generateLog("Inside Document_Attach()");
			String WItemID="1";
			String Volid=objWF.VolumeID;
			String []flag_file;
			volIdShort= Short.parseShort(objWF.VolumeID);
			Log.generateLog("volIdShort-->> "+volIdShort);
			//String sAppNo = "";
			/*String strSelectQuery = "SELECT "+objWF.pidColumn+","+objWF.fileMatchParam+" FROM "+objWF.AccountTable +" WHERE "+objWF.pidColumn+" = '"+winame+"'";
			String inpXML_APSelect = XMLGen.APSelectWithColumnNames(objWF.cabinetName,objWF.sessionID,strSelectQuery);
			Log.generateLog("Before APSelect Call");
			String outXml_APSelect = objWF.execute(inpXML_APSelect);
			xmlParser.setInputXML(outXml_APSelect);
			if (xmlParser.getValueOf("MainCode").equals("0")) {
				Log.generateLog("APSelect Call Success");
				sAppNo = xmlParser.getValueOf(objWF.fileMatchParam);
			}
			else {
				Log.generateLog("Could not fetch Application Number");
			
			}*/
			
			if(sAppNo != null) {
				String status = null;
				File appFolder = new File(folder+"\\"+sAppNo+"\\");
				Log.generateLog("Folder from where document will be fetched is::"+appFolder);
				if(appFolder.isDirectory()) {
					File[] listOfFiles =appFolder.listFiles();
					String[] fileNames = appFolder.list();
					flag_file=new String[listOfFiles.length];
					Log.generateLog("Document_Attach--->>>No of documents--->>>"+listOfFiles.length);
					for(int i =0; i < listOfFiles.length; i++) {
						//Log.generateLog(" FileName: "+fileNames[i]);
						int PageCount=Tif6.getPageCount(listOfFiles[i].toString());
						if(objWF.docTypeSingle.indexOf(fileNames[i])  > -1) {
							Log.generateLog("Valid single instance type document");
							//PageCount=Tif6.getPageCount(fileNames[i].toString());
							Log.generateLog("Number of pages: "+PageCount);
							status = Add_Document(listOfFiles[i],winame,Volid,sAppNo,PageCount);
							Log.generateLog("Status is "+status);
						}
						else{
							int k = fileNames[i].lastIndexOf("_");
							String tempName = "";
							if(k > 0) {
								tempName = fileNames[i].substring(0,k); 
							}
							Log.generateLog("File name::: "+tempName);
							if(objWF.docTypeMultiple.indexOf(tempName)  > -1) {
								Log.generateLog("Valid multiple instance type document");
								Log.generateLog("Number of pages: "+PageCount);
								status = Add_Document(listOfFiles[i],winame,Volid,sAppNo,PageCount);
								Log.generateLog("Status is "+status);
							}
							else {
								Log.generateLog("Ivalid document: "+fileNames[i]);
								status = "FAILED";
								Log.generateLog("Status is "+status);
							}
						}
						if(status.equals("SUCCESS")) {
							Log.generateLog("Setting flag_file T");
							flag_file[i] = "T";
						}
						else {
							Log.generateLog("Setting flag_file F");
							flag_file[i] = "F";
						}
						//File currentFile = new File(folder+"\\"+Agent_Code[i]+".pdf");
					}
					if(listOfFiles.length > 0) {
						if(allTrue(flag_file).equals("T"))
						{
							if(objWF.autoCompFlag.equals("Y"))
							{
								if(lockWorkItem(winame,WItemID).equals("0")){
									String WIInputXml=XMLGen.WMCompleteWorkItem(objWF.cabinetName,objWF.sessionID,winame,WItemID,"A");
									//Log.generateLog("Complete Workitem input"+WIInputXml);
									String WIOutputXml=this.objWF.execute(WIInputXml);
									xmlParser.setInputXML(WIOutputXml);
									String mainCode = xmlParser.getValueOf("MainCode");
									if(mainCode.equals("0")) {
										Log.generateLog("Complete Workitem Output"+WIOutputXml);
										Log.generateLog("Completed Successfully!!");
										System.out.println("Workitem "+winame+" Completed Successfully!!");
									}else if(mainCode.equals("11")) {
										Log.generateLog("Invalid Session");
										objWF.connectToWorkFlow("N");
										WIInputXml=XMLGen.WMCompleteWorkItem(objWF.cabinetName,objWF.sessionID,winame,WItemID,"A");
									}
									else{
										Log.generateLog("Error in Complete Workitem"+WIOutputXml);
										System.out.println("Error in Complete Workitem. Check logs for details");
									}
								}else {
									Log.generateLog("Error in lock work item");
									System.out.println("Error in lock work item. Check logs for details");
								}															
							}else
							{
								Log.generateLog("Document Add successfully no workitem complete operation in this case!!");
							}
						}
					}
					else {
						System.out.println("No document present to upload for application number: "+sAppNo);
						Log.generateLog("No document present to upload for application number: "+sAppNo);
					}
				}
				else {
					Log.generateLog("No folder for application number "+sAppNo+"exists.");
				}
				
			}
			
			
		}catch(Exception ex) {			
			//System.out.println(StackTraceToString(ex));
			Log.generateLog("Document_Attach: "+StackTraceToString(ex));
			return "99";
		}
		
	
		return "0";
	}
	
	
	private String Document_Replace(File folder,String winame,String sAppNo) {
		try {
			Log.generateLog("Inside Document_Replace()");
			String WItemID="1";
			String Volid=objWF.VolumeID;
			String []flag_file;
			String status = "";
			volIdShort= Short.parseShort(objWF.VolumeID);
			Log.generateLog("volIdShort-->> "+volIdShort);
			String pFolderIndex = isindexfunc(winame);
			JPISIsIndex IsIndex = new JPISIsIndex();
			if(pFolderIndex==null && pFolderIndex==""){
				Log.generateLog("Error while fetching itemIndex for woritem No.");
			}
			else {
				String inpXML = "<?xml version=\"1.0\"?>\n"
								+"<NGOGetDocumentListExt_Input>"
								+"<Option>NGOGetDocumentListExt</Option>"
								+"<CabinetName>"+objWF.cabinetName+"</CabinetName>"
								+"<UserDBId>"+objWF.sessionID+"</UserDBId>"
								+"<CurrentDateTime></CurrentDateTime>"
								+"<FolderIndex>"+pFolderIndex+"</FolderIndex>"
								+"<OrderBy>2</OrderBy>"
								+"<SortOrder>A</SortOrder><DataAlsoFlag>N</DataAlsoFlag>"
								+"<DocumentType>I</DocumentType>"
								+"<LinkDocFlag>N</LinkDocFlag>"
								+"<AnnotationFlag>N</AnnotationFlag>"
								+"<ThumbNailAlsoFlag>N</ThumbNailAlsoFlag>"
								+"</NGOGetDocumentListExt_Input>";
								
				String outXML = objWF.execute(inpXML);	    	
				xmlParser.setInputXML(outXML);
				WFXmlResponse objXmlResponse = new WFXmlResponse(outXML);
				WFXmlList objList=null;
				ArrayList tempDocList = new ArrayList();
				ArrayList tempDocIndexList = new ArrayList();
				ArrayList tempDocISList = new ArrayList();
				
				
				if (xmlParser.getValueOf("Status").equals("0")) {
					if(Integer.parseInt(xmlParser.getValueOf("TotalNoOfRecords")) > 0) {
						for(objList=objXmlResponse.createList("Documents", "Document");objList.hasMoreElements();objList.skip()) {
							tempDocList.add(objList.getVal("DocumentName"));
							tempDocIndexList.add(objList.getVal("DocumentIndex"));
							String tempVar = objList.getVal("ISIndex");
							tempVar = tempVar.substring(0,tempVar.indexOf("#"));
							tempDocISList.add(tempVar);
						}
						File appFolder = new File(folder+"\\"+sAppNo+"\\");
						Log.generateLog("Folder from where document will be fetched is::"+appFolder);
						if(appFolder.isDirectory()) {
							File[] listOfFiles =appFolder.listFiles();
							String[] fileNames = appFolder.list();
							flag_file=new String[listOfFiles.length];
							Log.generateLog("Document_Attach--->>>No of documents--->>>"+listOfFiles.length);
							for(int i =0; i < listOfFiles.length; i++) {
								//Log.generateLog(" FileName: "+fileNames[i]);
								int PageCount=Tif6.getPageCount(listOfFiles[i].toString());
								if(objWF.docTypeSingle.indexOf(fileNames[i])  > -1) {
									Log.generateLog("Valid single instance type document");
									Log.generateLog("Number of pages: "+PageCount);
									//status = Add_Document(listOfFiles[i],winame,Volid,sAppNo,PageCount);
									String name = fileNames[i].substring(0,fileNames[i].lastIndexOf("."));
									int ind = tempDocList.indexOf(name);
									Log.generateLog("ind: "+ind+" fileNames[i] "+name );
									if(ind > -1) {
										Log.generateLog("Document already present. Need to overwrite");
										inpXML = XMLGen.NGODeleteDocumentExt_Input(objWF.cabinetName,objWF.sessionID,pFolderIndex,(String) tempDocIndexList.get(ind));
										outXML = objWF.execute(inpXML);
										xmlParser.setInputXML(outXML);
										if(xmlParser.getValueOf("Status").equals("0")) {
											IsIndex.m_nDocIndex = Integer.parseInt(tempDocISList.get(ind).toString());
											IsIndex.m_sVolumeId = Short.parseShort(objWF.VolumeID);
											//CPISDocumentTxn.DeleteDoc_MT(null, objWF.serverIP, Short.parseShort(Integer.toString(objWF.serverPort)), objWF.cabinetName, IsIndex);
											Log.generateLog("Calling Add_Document()");
											status = Add_Document(listOfFiles[i],winame,Volid,sAppNo,PageCount);
										
										}
										else {
											Log.generateLog("Could not delete document");
										}
									}
									else {
										status = Add_Document(listOfFiles[i],winame,Volid,sAppNo,PageCount);
									
									}
									Log.generateLog("Status is "+status);
								}
								else{
									int k = fileNames[i].lastIndexOf("_");
									String tempName = "";
									if(k > 0) {
										tempName = fileNames[i].substring(0,k); 
									}
									Log.generateLog("File name::: "+tempName);
									if(objWF.docTypeMultiple.indexOf(tempName)  > -1) {
										Log.generateLog("Valid multiple instance type document");
										Log.generateLog("Number of pages: "+PageCount);
										//status = Add_Document(listOfFiles[i],winame,Volid,sAppNo,PageCount);
										String name = fileNames[i].substring(0,fileNames[i].lastIndexOf("."));
										int ind = tempDocList.indexOf(name);
										Log.generateLog("ind: "+ind+" fileNames[i] "+name );
										if(ind > -1) {
											Log.generateLog("Document already present. Need to overwrite");
											inpXML = XMLGen.NGODeleteDocumentExt_Input(objWF.cabinetName,objWF.sessionID,pFolderIndex,(String) tempDocIndexList.get(ind));
											outXML = objWF.execute(inpXML);
											xmlParser.setInputXML(outXML);
											if(xmlParser.getValueOf("Status").equals("0")) {
												IsIndex.m_nDocIndex = Integer.parseInt(tempDocISList.get(ind).toString());
												IsIndex.m_sVolumeId = Short.parseShort(objWF.VolumeID);
												Log.generateLog("Calling DeleteDoc_MT()");
												//CPISDocumentTxn.DeleteDoc_MT(null, objWF.serverIP, Short.parseShort(Integer.toString(objWF.serverPort)), objWF.cabinetName, IsIndex);
												Log.generateLog("Deleted from doc...calling Add_Document()");
												status = Add_Document(listOfFiles[i],winame,Volid,sAppNo,PageCount);
											
											}
											else {
												Log.generateLog("Could not delete document");
											}
										}
										else {
											status = Add_Document(listOfFiles[i],winame,Volid,sAppNo,PageCount);
										
										}
										Log.generateLog("Status is "+status);
									}
									else {
										Log.generateLog("Ivalid document: "+fileNames[i]);
										status = "FAILED";
										Log.generateLog("Status is "+status);
									}
								}
								if(status.equals("SUCCESS")) {
									Log.generateLog("Setting flag_file T");
									flag_file[i] = "T";
								}
								else {
									Log.generateLog("Setting flag_file F");
									flag_file[i] = "F";
								}
							}
							if(listOfFiles.length > 0) {
								if(allTrue(flag_file).equals("T")) {
									if(objWF.autoCompFlag.equals("Y")) {
										if(lockWorkItem(winame,WItemID).equals("0")){
										String WIInputXml=XMLGen.WMCompleteWorkItem(objWF.cabinetName,objWF.sessionID,winame,WItemID,"A");
										//Log.generateLog("Complete Workitem input"+WIInputXml);
										String WIOutputXml=this.objWF.execute(WIInputXml);
										xmlParser.setInputXML(WIOutputXml);
										String mainCode = xmlParser.getValueOf("MainCode");
										if(mainCode.equals("0")) {
											Log.generateLog("Complete Workitem Output"+WIOutputXml);
											Log.generateLog("Completed Successfully!!");
											System.out.println("Workitem "+winame+" Completed Successfully!!");
										}else if(mainCode.equals("11")) {
											Log.generateLog("Invalid Session");
											objWF.connectToWorkFlow("N");
											WIInputXml=XMLGen.WMCompleteWorkItem(objWF.cabinetName,objWF.sessionID,winame,WItemID,"A");
										}
										else{
											Log.generateLog("Error in Complete Workitem"+WIOutputXml);
											System.out.println("Error in Complete Workitem. Check logs for details");
										}
										}else {
											Log.generateLog("Error in lock work item");
											System.out.println("Error in lock work item. Check logs for details");
										}															
									}else {
										Log.generateLog("Document Add successfully no workitem complete operation in this case!!");
									}
								}
							}
							else {
								System.out.println("No document present to upload for application number: "+sAppNo);
								Log.generateLog("No document present to upload for application number: "+sAppNo);
							}
						}							
					}
					else {
						Log.generateLog("No document attached with workitem");
						status = Document_Attach(folder,winame,sAppNo);	//simply add documnets
					}
				
				}
				else {
					Log.generateLog("Error while fetching documents");
				}
			}
		}catch(Exception ex) {			
			//System.out.println(StackTraceToString(ex));
			Log.generateLog("Document_Replace: "+StackTraceToString(ex));
			return "99";
		} /*catch (JPISException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return "0";
	}
	
	public String allTrue(String[] array) {
		Log.generateLog("Inside allTrue function");
		for (int i=0; i<array.length; i++) {
			Log.generateLog("inside for loop of fucntion value is::"+array[i]);
			if ((array[i].equals("F"))) {
			   return "F";
			}
		}
		return "T";
	}
	
	private String Add_Document(File currentFile, String winame,String Volid,String AppNum, int pgCount) {
		JPISIsIndex IsIndex = new JPISIsIndex();
		Log.generateLog("IsIndex--->>"+IsIndex);
		JPDBRecoverDocData oRecoverDocData = new JPDBRecoverDocData();
		//N for Non Image Files and I for Image Files i.e. tif files.	
		oRecoverDocData.m_cDocumentType = 'I'; 
		oRecoverDocData.m_nDocumentSize = (int)(currentFile).length();
		oRecoverDocData.m_sVolumeId =Short.parseShort(Volid);
		String strDocSize=String.valueOf(oRecoverDocData.m_nDocumentSize);
		Log.generateLog("strDocSize--->>"+strDocSize);
		String AddDocResult=ADDDocument_MT(currentFile.getAbsolutePath(), oRecoverDocData, IsIndex,winame,currentFile,AppNum,pgCount);
		if(AddDocResult.equals("-1")){
			System.out.println("Error inside ADDDocument_MT function. please check Logs for more Information!!");
			Log.generateLog("Error inside Add document function() please check Logs for more Information!!");
			return "FAILED";
		}
		else if(AddDocResult.equals("Failed NGOAddDocument")){
			System.out.println("Error inside Add document function() please check Logs for more Information!!");
			Log.generateLog("Error inside Add document function() please check Logs for more Information!!");
			return "FAILED";
		}
		else if(AddDocResult.equals("0")){
			Log.generateLog("Document Add successfully!!");
			System.out.println("Document name "+currentFile.getName()+" Uploaded Successfully for WI No:"+winame);
			return "SUCCESS";
		}
		else{
			Log.generateLog("Error in ADDDocument_MT()--->>"+AddDocResult);
			System.out.println("Error in ADDDocument_MT()--->>"+AddDocResult);
			Log.generateLog("ERROR");
			return "FAILED";
		}
	}
	
	private String ADDDocument_MT(String strFilePath, JPDBRecoverDocData oRecoverDocData, JPISIsIndex IsIndex,String winame,File file, String AppNum, int pgCount){
		String sErrorDesc="0";
		//boolean success =false;
		File dir=null;
		Date time= new Date(); 
		int mname=time.getMonth()+1;
		String strDocType = file.getName().replaceFirst("[.][^.]+$","");
		//String fname=time.getDate()+"-"+mname+"-"+time.getHours()+"Hr-"+time.getMinutes()+"Mn-"+time.getSeconds()+"Sc";
		String Failedpath=objWF.failurePath;	
		String DestinationPath=objWF.successPath;
		//Log.generateLog("So"+So);
		String SourcePath=strFilePath;
		try{						
			//Log.generateLog("check port number from helper"+UtilityHelper.serverPort);
			volIdShort= Short.parseShort(objWF.VolumeID);
			Log.generateLog("volIdShort-->> "+volIdShort);
			Log.generateLog("strFilePath "+strFilePath);
			Log.generateLog("oRecoverDocData "+oRecoverDocData);
			Log.generateLog("IsIndex "+IsIndex);			
			CPISDocumentTxn.AddDocument_MT(null,objWF.serverIP,Short.parseShort(Integer.toString(objWF.serverPort)),objWF.cabinetName,volIdShort,strFilePath,oRecoverDocData,"", IsIndex);
			DocIDX=""+IsIndex.m_nDocIndex;
			String 	ParentFolderIndex=isindexfunc(winame);
			Log.generateLog("ParentFolderIndex value is"+ParentFolderIndex);
				if(ParentFolderIndex==null && ParentFolderIndex==""){
					Log.generateLog("Error while fetching itemIndex for woritem No.");
					sErrorDesc="-1";
				}
				else{						
						long file_length=file.length();						
						String	sInputXML=XMLGen.NGOAddDocument_Input(objWF.cabinetName,objWF.sessionID,ParentFolderIndex,strDocType,objWF.VolumeID,DocIDX,volIdShort,file_length,objWF.fileType,pgCount,"N","I");						
						Log.generateLog("sInputXML: NGOAddDocument "+sInputXML);
						String sOutputXML=objWF.execute(sInputXML);
						Log.generateLog("sOutputXML:NGOAddDocument "+sOutputXML);
						
							if(sOutputXML.equals("") || Integer.parseInt(sOutputXML.substring(sOutputXML.indexOf("<Status>")+8 , sOutputXML.indexOf("</Status>")))!=0){
								Log.generateLog("Error while adding document to Omnidocs NGOAddDocument.");	
								sErrorDesc="Failed NGOAddDocument";
								Log.generateLog(sErrorDesc);
								sErrorDesc=copyanddelete_failure(SourcePath,DestinationPath,Failedpath,file,AppNum);
								Log.generateLog("Error is of copyanddelete_failure-->>"+sErrorDesc);
								sErrorDesc="Failed NGOAddDocument";
								
							}
							else
							{
								System.out.println("Document attached to workitem"+winame);
								//Added by kapil to move file and delete orinigal one
								sErrorDesc=copyanddelete(SourcePath,DestinationPath,Failedpath,file,AppNum);
								Log.generateLog("Error is of copyanddelete-->>"+sErrorDesc);
							}	
							
						return sErrorDesc;	
				}
		}catch(JPISException jpisexception){
			sErrorDesc="-1";
			Log.generateLog("jpisexception : "+jpisexception.toString());
			StringWriter sw = new StringWriter();
			jpisexception.printStackTrace(new PrintWriter(sw));
			String stackTrace = sw.toString();	
			Log.generateLog("jpisexception : "+stackTrace);
			if(jpisexception.toString().indexOf("Server Down")>=0){
				sErrorDesc="-1";							
			}
		}
		catch(Exception ex){
			Log.generateLog("Error is:::"+StackTraceToString(ex));
			sErrorDesc="-1";
		}
		return sErrorDesc;
	}
	
	private String lockWorkItem(String PID,String WItemID) {
		String mainCode=null;
		try {
			String inXml = XMLGen.get_WMGetWorkItem_Input(objWF.cabinetName,objWF.sessionID, PID, WItemID);
			String outXml = this.objWF.execute(inXml);
			xmlParser.setInputXML(outXml);
			mainCode = xmlParser.getValueOf("MainCode");
			if(mainCode.equals("0")) {
				Log.generateLog(PID+" WorkItem locked successfully");
			} else if(mainCode.equals("11")) {
				Log.generateLog("Invalid Session in lock WI "+PID);
				objWF.connectToWorkFlow("N");
				return lockWorkItem(PID,WItemID);
			} else {
				Log.generateLog(PID+" WorkItem locked failed with maincode : "+mainCode);
				Log.generateLog("lockWorkItem inXml : "+inXml); 
				Log.generateLog("lockWorkItem outXml : "+outXml);
			}
		} catch(Exception e) {
			Log.generateLog("lockWorkItem: "+StackTraceToString(e));
		}
		return mainCode; 
	}
	
	public String isindexfunc(String winame1)
	{
	
		String Query_itemindex="select FolderIndex from pdbfolder where name='"+winame1+"'";	
		String sInputXML = XMLGen.APSelectWithColumnNames(objWF.cabinetName,objWF.sessionID,Query_itemindex);
		//logger.info("sInputXML: "+sInputXML);	
		String sOutputXML=objWF.execute(sInputXML);
		//logger.info("sOutputXML: "+sOutputXML);
		if(sOutputXML.equals("") || Integer.parseInt(sOutputXML.substring(sOutputXML.indexOf("<MainCode>")+10 ,sOutputXML.indexOf("</MainCode>")))!=0){													
		  Log.generateLog("Error while fetching itemIndex for woritem No. "+winame1);
			return null	;	
		}else{
		  WFXmlResponse xmlResponse = new WFXmlResponse(sOutputXML);
		  WFXmlList objList = xmlResponse.createList("Records","Record");
		  String sItemIndex=objList.getVal("FOLDERINDEX");
		  Log.generateLog("FolderIndex: "+sItemIndex);
		  return sItemIndex;	
		}	
	}
	
	
	public String copyanddelete(String Source,String Destination,String Fail,File file, String AppNum){
		try{		
			Log.generateLog("Inside copy function");
			Log.generateLog("Inside Source function"+Source);  
			Log.generateLog("Inside Destination function"+Destination);	
			Log.generateLog("Inside file.getName() function"+file.getName());	
			File afile =new File(Source);	
		//	File bfile =new File(Destination); 		
		//changes done for creating subfolder's
			File dir=null; 	
			Date time= new Date();
			SimpleDateFormat simpleDateformat=new SimpleDateFormat("yyyy");
			String fname=time.getDate()+"-"+(time.getMonth()+1)+"-"+simpleDateformat.format(time);
			Log.generateLog("fname--->>>"+fname);
			dir = new File(Destination+"\\"+fname+"\\"+AppNum);
			File bfile =new File(dir+"\\"+file.getName()); 
			if(!dir.exists()){
			Log.generateLog("no succes folder. create "+dir.getAbsolutePath());
				try{
					if(dir.mkdirs())	{
						Log.generateLog("succes folder created");
					}
					else{
						Log.generateLog(" succes folder creation failed");
						return "-1";
					}
				}
				catch(Exception e){
					Log.generateLog("exception in suucces folder creation "+e.toString());
					e.printStackTrace();
					return "-1";
				}
			}
//change end here for creatibng subfolder
			//File bfile =new File(Destination); 
			
			FileInputStream inStream = new FileInputStream(afile);
			FileOutputStream outStream = new FileOutputStream(bfile);	 
			byte[] buffer = new byte[1024];	 
			int length;
			//copy the file content in bytes 
			while ((length = inStream.read(buffer)) > 0){ 
				outStream.write(buffer, 0, length); 
			} 
			inStream.close();
			outStream.close(); 
			//delete the original file
			Log.generateLog("Delete the original file");
			afile.delete(); 
			System.out.println("File is transfer successful!"); 
			return "0";
		}catch(IOException e){
				System.out.println("Error while transfer File is"); 
				Log.generateLog("Error inside copy and delete");
				e.printStackTrace();
				return copyanddelete_failure(Source,Destination,Fail,file,AppNum);					
		}
	}
	
	public String copyanddelete_failure(String Source,String Destination,String Fail,File file, String AppNum){		
			try{
				Log.generateLog("inside copyanddelete_failure");
				File afile =new File(Source);
				//File bfile =new File(Fail);
//changes done for creating subfolder's				
				File dir=null; 	
				Date time= new Date();
				SimpleDateFormat simpleDateformat=new SimpleDateFormat("yyyy");
				String fname=time.getDate()+"-"+(time.getMonth()+1)+"-"+simpleDateformat.format(time);
				Log.generateLog("fname--->>>"+fname);
				dir = new File(Fail+"\\"+fname+"\\"+AppNum);
				File bfile =new File(dir+"\\"+file.getName()); 
				if(!dir.exists()){
				Log.generateLog("no succes folder. create "+dir.getAbsolutePath());
					try{
						if(dir.mkdirs())	{
							Log.generateLog("succes folder created");
						}
						else{
							Log.generateLog(" succes folder creation failed");
							return "-1";
						}
					}
					catch(Exception e){
						Log.generateLog("exception in suucces folder creation "+e.toString());
						e.printStackTrace();
						return "-1";
					}
				}
				FileInputStream inStream = new FileInputStream(afile);
				FileOutputStream outStream = new FileOutputStream(bfile);	 
				byte[] buffer = new byte[1024];	 
				int length;
				//copy the file content in bytes 
				while ((length = inStream.read(buffer)) > 0){ 
					outStream.write(buffer, 0, length); 
				} 
				inStream.close();
				outStream.close(); 
				//delete the original file
				afile.delete();	
				return "-1";
			}catch(Exception ex)
			{
				System.out.println("Error while transfer File is"); 
				Log.generateLog("Error inside copy and delete");
				ex.printStackTrace();; 
				return "-1";
			}	
	}	
	

	private String StackTraceToString(Exception e)
	{
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		return result.toString();
	}
	 public static String getDateString() {
        Calendar calendar = new GregorianCalendar();
        return "" + calendar.get(Calendar.DAY_OF_MONTH) +
                (calendar.get(Calendar.MONTH) + 1) + calendar.get(Calendar.YEAR);
    }
	 public  String APProcedure(String strEngineName, String strSessionId, String UserName, String strProcName, String strParams)
	 {
	  return "<?xml version=\"1.0\"?>" +
	    "<APProcedure_Input>" +
	    "<Option>APProcedure</Option>" +
	    "<SessionId>" + strSessionId + "</SessionId>" +
	    "<ProcName>" + strProcName + "</ProcName>" +
	    "<Params>" + strParams + "</Params>" +
	    "<UserName>"+ UserName + "</UserName>" +
	    "<EngineName>" + strEngineName + "</EngineName>" +
	    "<APProcedure_Input>";
	   }
		
	
	public String ChangeDateFormat(String obj)
	{
		try
	        {
			Date parsed = null;
			SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			format.setLenient(false);
			SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yyyy");
			  String modifiedDate = "";
	                if(obj.equals(""))
	                {
	                        return "";
	                }

					parsed = format.parse(obj);
					modifiedDate = format2.format(parsed);
					Log.generateLog("uploadLog Date before parsed ::: " + obj);
					Log.generateLog("uploadLog Parsed Date ::: " + modifiedDate);
				   return(modifiedDate);
			}
	        catch(Exception e)
	        {
	        	Log.generateLog("uploadLog Exception on parsing Date:::" + e.getMessage());
	                return "false";
	        }
	}

    private void reconnectToWorkflow() {
        try 
		{
            objWF.disconnectFromWorkFlow();
			Log.generateLog("Disconnected from WF...");
            objWF.disconnectFromServer();
			Log.generateLog("Disconnected from Server...");
			bConnected=false;
        } catch (Exception ex) {
            Log.generateLog(StackTraceToString(ex));
        }
        try {
            if (objWF.connectToServer()) 
			{
				Log.generateLog("Connected to Server...");
                String str = objWF.connectToWorkFlow("N");
                bConnected=true;
				String temp[] = str.split("~");
                if (!temp[0].equals("0")) 
				{
					bConnected=false;
                    Thread.sleep(10000);
                    if (keepRunning) 
					{
                        reconnectToWorkflow();
                    }
                }
            }
        } catch (Exception ex) {
            Log.generateLog(StackTraceToString(ex));
        }
    }
	/*private void ConnectToWorkflow() {
        try 
		{
        	System.out.println("covhnvpppppppppp");
            if (objWF.connectToServer()) 
			{
            	System.out.println("ppppppppppconnnnnn");
                String str = objWF.connectToWorkFlow("N");
				bConnected=true;
				String temp[] = str.split("~");
                if (!temp[0].equals("0")) 
				{
                    Thread.sleep(10000);
                    if (keepRunning) 
					{
                        reconnectToWorkflow();
                    }
                }
            }
        } catch (Exception ex) 
		{
            Log.generateLog(StackTraceToString(ex));
        }
    }*/

  
    void waiteloop(long wtime) {
        try {
            for (int i = 0; i < 10; i++) {
                Thread.yield();
                Thread.sleep(wtime / 10);
                if (!keepRunning) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            Log.generateLog(StackTraceToString(e));
        }
    }



    String[] StringtoArray(String s, String sep) {
        // convert a String s to an Array, the elements
        // are delimited by sep
        StringBuffer buf = new StringBuffer(s);
        int arraysize = 1;
        for (int i = 0; i < buf.length(); i++) {
            if (sep.indexOf(buf.charAt(i)) != -1) {
                arraysize++;
            }
        }
        String[] elements = new String[arraysize];
        int y, z = 0;
        if (buf.toString().indexOf(sep) != -1) {
            while (buf.length() > 0) {
                if (buf.toString().indexOf(sep) != -1) {
                    y = buf.toString().indexOf(sep);
                    if (y != buf.toString().lastIndexOf(sep)) {
                        elements[z] = buf.toString().substring(0, y);
                        z++;
                        buf.delete(0, y + 1);
                    } else if (buf.toString().lastIndexOf(sep) == y) {
                        elements[z] = buf.toString().substring(0,
                                buf.toString().indexOf(sep));
                        z++;
                        buf.delete(0, buf.toString().indexOf(sep) + 1);
                        elements[z] = buf.toString();
                        z++;
                        buf.delete(0, buf.length());
                    }
                }
            }
        } else {
            elements[0] = buf.toString();
        }
        buf = null;
        return elements;
    }
}
