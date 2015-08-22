/*------------------------------------------------------------------------------------------------------
												NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group: Application -Projects

Project/Product:  Bandhan AO LOS Process

Application: Bulk Documnet Upload Utility

Module: autoserver pakage

File Name: Log.java

Author: Divya Gautam

Date (DD/MM/YYYY): 15/05/2015

Description:Class for generating logs.

-------------------------------------------------------------------------------------------------------

CHANGE HISTORY

-------------------------------------------------------------------------------------------------------

Problem No/CR No   Change Date   Changed By    Change Description

------------------------------------------------------------------------------------------------------*/
package com.newgen.autoserver;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.*;
import java.io.*;


public class Log {

    private FileWriter out;
    private static String logName = "Log";
    private static Log logFile;
    private static String logDate = "";
    private static Calendar calendar;
    private static String logPath = System.getProperty("user.dir");
	private static int version = 1;
	private static int iCount=1;

	public static void moveFile(String DestFolder, String FileName, String FolderPath)
	{
		Runtime r = Runtime.getRuntime();
		r.gc();
		
		try
		{
			File DestFolderPath = new File(FolderPath.substring(0,FolderPath.lastIndexOf(File.separator))+File.separator+DestFolder);
			if(!DestFolderPath.exists())
				DestFolderPath.mkdir();
			DestFolderPath = null;
			String FilePath=FolderPath+File.separator+FileName;
			File DestFilePath=new File(FolderPath.substring(0,FolderPath.lastIndexOf(File.separator))+File.separator+DestFolder+File.separator+FileName);
			
			File srcFile = new File(FilePath);
			boolean success = srcFile.renameTo(DestFilePath);
			if (!success) 
			{
				// File was not successfully moved
				if(DestFilePath.exists())
					Log.generateLog("Failure while moving. File already Exists at Destination Folder."); 
				else
					Log.generateLog("Failure while moving "+ srcFile.getAbsolutePath()+ " to " +DestFilePath.getAbsolutePath()+"==="+srcFile.canWrite());
			}
			else
			{
				Log.generateLog("Success while moving " + srcFile.getAbsolutePath()+ " to " +DestFilePath.getAbsolutePath());
			}
			srcFile=null;
			DestFilePath = null;
		}
		catch(Exception e)
		{
			Log.generateLog("Exception occurred while moving :"+e.toString());
		}
	}

  

	public static void Move(String s,File f,String destfolder){
		try{							
				// Destination directory
				File dir = new File(s+File.separator+destfolder);
				if(!dir.exists())
						dir.mkdir();
				//delete destination file if it already exists
				//////////////
				File fTemp=new File(s+File.separator+destfolder+File.separator+f.getName());
				if(fTemp.exists())
				{
					if(!fTemp.isDirectory())
						fTemp.delete();
					else
						deleteDir(fTemp);
				}
				else
					fTemp=null;
				//////////////

				// Move file to new directory
				//boolean success = f.renameTo(new File(dir, f.getName()));
				boolean success = f.renameTo(new File(dir, f.getName()));
				if (!success) {
					// File was not successfully moved
					System.out.println("Failure while moving "+ f.getName()+ " to " +destfolder+"==="+f.canWrite());
					Log.generateLog("Failure while moving "+ f.getAbsolutePath()+ " to " +dir.getAbsolutePath()+"==="+f.canWrite());
				}
				else
				{
					//System.out.println("Success while moving "+ f.getName()+ " to " +destfolder);
					Log.generateLog("Success while moving "+ f.getName()+ " to " +destfolder);
				}
				/////////////////////////////////
		}catch(Exception e)
		{
			//exception occurred while moving file to Error
			System.out.println("Exception occurred while moving "+ f.getName()+ " to " +destfolder +":"+e.toString());
			Log.generateLog("Exception occurred while moving "+ f.getName()+ " to " +destfolder +":"+e.toString());
		}
	}
	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        // The directory is now empty so delete it
		return dir.delete();
    }

	public static void generateSummaryLog(String strOutput, String FileName) 
	{
		StringBuffer str = new StringBuffer();
		//str.append(DateFormat.getDateTimeInstance(0,2).format(new java.util.Date()));
		str.append("\n");
		str.append(strOutput);
		str.append("\n");

		StringBuffer strFilePath = null;
		String tmpFilePath="";

		Calendar calendar=new GregorianCalendar();
		String DtString=String.valueOf(""+calendar.get(Calendar.DAY_OF_MONTH) +(calendar.get(Calendar.MONTH) + 1) + calendar.get(Calendar.YEAR));
		try 
		{
			strFilePath = new StringBuffer(50);
			strFilePath.append(GlobalVar.logPath);
			strFilePath.append(File.separatorChar);
			strFilePath.append("Summary_"+FileName+".xml");
			tmpFilePath = strFilePath.toString();
			
			//PrintStream out = new PrintStream(new FileOutputStream(tmpFilePath), true);
			BufferedWriter out = new BufferedWriter(new FileWriter(tmpFilePath, true));
			
			out.write(str.toString());
			out.close();
			out = null;
		} 
		catch (Exception exception) 
		{
			System.out.println(exception.toString());
		} 
		finally 
		{
			strFilePath = null;
		}
	}
	public static void generateLog(String strOutput) {
        try {
            if (logDate.equals(getDateString())) 
			{
				//*************************
				
				File Logdir = new File(logPath);
				String[] ChildDir=Logdir.list();
				String strLgFlName="";
				if(ChildDir==null || ChildDir.length==0)
				{
					//Log.generateLog("Invalid directory or Directory empty");
				}
				else
				{
					for (int k=0; k<ChildDir.length ; k++)
					{
						if(ChildDir[k].indexOf(getDateString()+"_"+iCount)!=-1)
						{
							strLgFlName=ChildDir[k];//fileName.substring(fileName.lastIndexOf(File.separator)+1,fileName.length())+ "_"+dateLog + "_"+iCount+ ".xml";
						}
						else
						{
						}
					}
				}
				
				File file=new File(logPath+File.separator+strLgFlName);
				long fileSize = file.length();
				if(fileSize>=(GlobalVar.strLogSize*1024*1024))
				{
					initilizeLog();
			         generateLog(strOutput);
				}
				//**********************************
				else
				{
					StringBuffer str = new StringBuffer();
	                str.append(DateFormat.getDateTimeInstance(0,2).format(new java.util.Date()));
					str.append("\n");
	                str.append(strOutput);
		            str.append("\n");
			        if (GlobalVar.printScreenflag == true)
						logFile.println(str.toString());
				}
            } else {
                initilizeLog();
				iCount=1;
                generateLog(strOutput);
            }
        } catch (FileNotFoundException fnf) {
            initilizeLog();
            generateLog(strOutput + "\n" + fnf.toString());
        } catch (IOException io) {
            initilizeLog();
            generateLog(strOutput + "\n" + io.toString());
        } catch (Exception e) {
			System.out.println("================"+e.toString());
            e.printStackTrace();
        }
    }

    public Log(String fileName, String dateLog) throws FileNotFoundException,
            IOException {
        //**********************************
		//System.out.println("filename=="+fileName.substring(0,fileName.lastIndexOf(File.separator)));
		File Logdir = new File(fileName.substring(0,fileName.lastIndexOf(File.separator)));
		String[] ChildDir=Logdir.list();
		String strLgFlName="";
		if(ChildDir==null || ChildDir.length==0)
		{
			//System.out.println("======first time");
			//Log.generateLog("Invalid directory or Directory empty");
		}
		else
		{
			//iCount=1;
			
			for (int k=0; k<ChildDir.length ; k++)
			{
				String strLogFileName=fileName.substring(fileName.lastIndexOf(File.separator)+1,fileName.length());
				//System.out.println(strLogFileName);
				strLogFileName=strLogFileName + "_" + dateLog +"_";
				if(ChildDir[k].indexOf(strLogFileName)!=-1)
				{
					try{
							if(iCount < Integer.parseInt(ChildDir[k].substring(ChildDir[k].lastIndexOf("_")+1,ChildDir[k].indexOf(".xml"))))
								iCount=Integer.parseInt(ChildDir[k].substring(ChildDir[k].lastIndexOf("_")+1,ChildDir[k].indexOf(".xml")));
							//System.out.println("==="+iCount);
							strLgFlName=fileName.substring(fileName.lastIndexOf(File.separator)+1,fileName.length())+ "_"+dateLog + "_"+iCount+ ".xml";
							//break;
					}catch(Exception e)
					{
						//System.out.println("==="+e.toString());
					}
				}
				else
				{
					//System.out.println("======else part");
				}

			}
		}
		//System.out.println("==="+iCount);
		File file = new File(logPath+File.separator+strLgFlName);
		//System.out.println("==="+logPath+File.separator+strLgFlName);
		long filesize = file.length();
		//System.out.println("filesize==="+filesize);

		if(filesize>=(GlobalVar.strLogSize*1024*1024))
			iCount++;
		//*********************************



        out = new FileWriter(fileName + "_" + dateLog +"_"+iCount+ ".xml", true);
		//iCount++;
        logDate = dateLog;
    }

    private void println(String x) throws FileNotFoundException, IOException {
        if (x != null) {
            out.write(x);
            out.write("\n");
            out.flush();
        }
    }

    public void closePrintStream() throws IOException {
        out.close();
    }

    private static void initilizeLog() {
        StringBuffer strFilePath = null;
        try {
            strFilePath = new StringBuffer(50);
            strFilePath.append(logPath);
            strFilePath.append(File.separatorChar);

            strFilePath.append(logName);
            logFile = initializeFile(strFilePath.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            strFilePath = null;
        }
    }

    private static Log initializeFile(String fileName) {
        Log fileWriter = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(
                "\n*************************************************************\nCreated On: ");
        strBuff.append(DateFormat.getDateTimeInstance(0,
                2).format(new java.util.Date()));
        try {
            fileWriter = new Log(fileName, getDateString());
            fileWriter.println(strBuff.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            fileName = null;
            strBuff = null;
            return fileWriter;
        }
    }

    public static String getDateString() {
        calendar = new GregorianCalendar();
        return "" + calendar.get(Calendar.DAY_OF_MONTH) +
                (calendar.get(Calendar.MONTH) + 1) + calendar.get(Calendar.YEAR);
    }
 public static String getDateTimeString() {
        calendar = new GregorianCalendar();
        return "" + calendar.get(Calendar.DAY_OF_MONTH) +
                (calendar.get(Calendar.MONTH) + 1) + calendar.get(Calendar.HOUR)+
			calendar.get(Calendar.MINUTE) + calendar.get(Calendar.MILLISECOND);
    }
    static {
        File dir = new File(logPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public static void setLogDir() {
        if (!GlobalVar.logPath.equals(logPath)) {
            try {
                logFile.closePrintStream();
            } catch (Exception ex) {}
            try {
                File destFile = new File(GlobalVar.logPath + File.separator +
                                         logName + "_" + getDateString() +
                                         ".xml");
                if (destFile.exists()) {
                    logFile = initializeFile(GlobalVar.logPath + File.separator +
                                             logName);
                    File currentFile = new File(logPath + File.separator +
                                                logName +
                                                "_" + getDateString() + ".xml");
                    BufferedReader reader = new BufferedReader(new FileReader(
                            currentFile));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        logFile.println(line);
                    }
                    reader.close();
                    currentFile.delete();
                    currentFile = null;
                } else {
                    File currentFile = new File(logPath + File.separator +
                                                logName +
                                                "_" + getDateString() + ".xml");
                    currentFile.renameTo(new File(GlobalVar.logPath +
                                                  File.separator + logName +
                                                  "_" + getDateString() +
                                                  ".xml"));
                }
            } catch (FileNotFoundException fnf) {
            } catch (IOException i) {
                i.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            logPath = GlobalVar.logPath;
        }
    }
}
