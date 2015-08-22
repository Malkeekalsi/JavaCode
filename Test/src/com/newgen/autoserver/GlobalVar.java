 /*------------------------------------------------------------------------------------------------------
												NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group: Application -Projects

Project/Product:  Bandhan AO LOS Process

Application: Bulk Documnet Upload Utility

Module: autoserver package

File Name: GlobalVar.java

Author: Divya Gautam

Date (DD/MM/YYYY): 15/05/2015

Description: Class for global variables.

-------------------------------------------------------------------------------------------------------

CHANGE HISTORY

-------------------------------------------------------------------------------------------------------

Problem No/CR No   Change Date   Changed By    Change Description

------------------------------------------------------------------------------------------------------*/
package com.newgen.autoserver;
import java.util.Hashtable;
public class GlobalVar {
    public static String logPath = System.getProperty("user.dir");
    public static int pollInterval = 20;
//	public static String BatchSize= "10";
    public static String titleName = "Upload To WorkFlow";
    public static String sortTitleName = "UploadToWF";
	public static boolean printScreenflag = true;
	public static String WFSServerIP = "";
	public static String AutoLogin = "AutoLogin";
	public static Hashtable ActID=new Hashtable();
	public static Hashtable LOSToOFMap=new Hashtable();
	public static Hashtable QueueMapping=new Hashtable();
	public static Hashtable ParallelAct=new Hashtable();
	public static Hashtable SequentialAct=new Hashtable();
	public static long strLogSize = 2;

}
