/*------------------------------------------------------------------------------------------------------
												NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group: Application -Projects

Project/Product:  Bandhan AO LOS Process

Application: Bulk Documnet Upload Utility

Module: XML Parser

File Name: XMLGen.java

Author: Divya Gautam

Date (DD/MM/YYYY): 15/05/2015

Description: Class for generating input xmls of omniflow and omnidocs calls.

-------------------------------------------------------------------------------------------------------

CHANGE HISTORY

-------------------------------------------------------------------------------------------------------

Problem No/CR No   Change Date   Changed By    Change Description

------------------------------------------------------------------------------------------------------*/
package com.newgen.autoserver.xml;

import com.newgen.wfdesktop.xmlapi.WFInputXml;

public class XMLGen {
	 private static XMLGenerator generator = new XMLGenerator();
    public XMLGen() {
    }

	 public static String UpdateExtTable(String strEngineName, String DSN,String SessionId, String TableName, String ColNames, String ColValues, String WhereClause)
	{
		return "<?xml version=\"1.0\"?>"+
			"<WFUpdate_output><Option>WFExtUpdate</Option>"+
			"<EngineName>"+strEngineName+"</EngineName>"+
			"<DataSourceName>"+DSN+"</DataSourceName>"+
			"<SessionId>"+SessionId+"</SessionId>"+
			"<TableName>"+TableName+"</TableName>"+
			"<ColName>"+ColNames+"</ColName>"+
			"<Values>"+ColValues+"</Values>"+
			"<WhereClause>"+WhereClause+"</WhereClause></WFUpdate_output>";
	}
	 public static String WFFetchInstrumentsList(String strEngineName,
                                                String strSessionId,
                                                String strCountFlag,
                                                String strQueueId,
                                                String strType,
                                                String strComparison,
                                                String strAttributeName,
                                                String strFilterString,
                                                String strLength,
                                                String strNoOfRecordsToFetch,
                                                String strOrderBy,
                                                String strSortOrder,
                                                String strLastValue,
                                                String strLastProcessInstance,
                                                String strLastWorkItemId,
                                                String strDataFlag) {
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("WMFetchWorkList", "Input");
        wfInputXml.appendTagAndValue("EngineName", strEngineName);
        wfInputXml.appendTagAndValue("SessionId", strSessionId);
        wfInputXml.appendTagAndValue("CountFlag", strCountFlag);
        wfInputXml.appendTagAndValue("DataFlag", strDataFlag);
        wfInputXml.appendTagAndValue("ZipBuffer", "Y");
        wfInputXml.appendTagStart("Filter");
        wfInputXml.appendTagAndValue("QueueId", strQueueId);
        wfInputXml.appendTagAndValue("Type", strType);
        wfInputXml.appendTagAndValue("Comparison", strComparison);
        wfInputXml.appendTagAndValue("AttributeName", strAttributeName);
        wfInputXml.appendTagAndValue("FilterString", strFilterString);
        wfInputXml.appendTagAndValue("Length", strLength);
        wfInputXml.appendTagEnd("Filter");
        wfInputXml.appendTagStart("BatchInfo");
        wfInputXml.appendTagAndValue("NoOfRecordsToFetch",
                                     strNoOfRecordsToFetch);
        wfInputXml.appendTagAndValue("OrderBy", strOrderBy);
        wfInputXml.appendTagAndValue("SortOrder", strSortOrder);
        wfInputXml.appendTagAndValue("LastValue", strLastValue);
        wfInputXml.appendTagAndValue("LastProcessInstance",
                                     strLastProcessInstance);
        wfInputXml.appendTagAndValue("LastWorkItem", strLastWorkItemId);
        wfInputXml.appendTagEnd("BatchInfo");
        wfInputXml.appendEndCallName("WMFetchWorkList", "Input");
        return wfInputXml.toString();
    } 
	
	
	public static String APSelectWithColumnNames(String strEngineName,
            String strSessionId, String strQuery) {
		WFInputXml wfInputXml = new WFInputXml();
		wfInputXml.appendStartCallName("APSelectWithColumnNames", "Input");
		wfInputXml.appendTagAndValue("EngineName", strEngineName);
		wfInputXml.appendTagAndValue("SessionId", strSessionId);
		wfInputXml.appendTagAndValue("Query", strQuery);
		wfInputXml.appendEndCallName("APSelectWithColumnNames", "Input");
		return wfInputXml.toString();
	}

  public static String WFOpenInstrumentsList(String strEngineName,
                                               String strWFSSession,
                                               String sQueueID,
                                               String strNoOfRecordToFetch) throws
            Exception {
        StringBuffer strbuff = new StringBuffer(100);
        strbuff.append(generator.createXMLHeader(1.0F));
        strbuff.append("<WFOpenInstrumentsList_Input>");
        strbuff.append(generator.writeElement("Option", "WMFetchWorkList"));
        strbuff.append(generator.writeElement("EngineName", strEngineName));
        strbuff.append(generator.writeElement("SessionId", strWFSSession));
        strbuff.append(generator.writeElement("CountFlag", "Y"));
        strbuff.append(generator.writeElement("QueueId", sQueueID));
        strbuff.append(generator.writeElement("DataFlag", "Y"));
        strbuff.append("<Filter>");
        strbuff.append(generator.writeElement("Type", "256"));
        strbuff.append("<AttributeName></AttributeName>");
        strbuff.append("<Comparison>0</Comparison>");
        strbuff.append("<FilterString></FilterString>");
        strbuff.append("<Length>0</Length>");
        strbuff.append("</Filter>");
        strbuff.append("<BatchInfo>");
        strbuff.append(generator.writeElement("NoOfRecordsToFetch",
                                              strNoOfRecordToFetch));
        strbuff.append(generator.writeElement("OrderBy", "1"));
        strbuff.append(generator.writeElement("SortOrder", "A"));
        strbuff.append("</BatchInfo>");
        strbuff.append("</WFOpenInstrumentsList_Inpu>");
        return strbuff.toString();
    }
    //this method is used for establish the connection with cabinet
    public static String get_WMConnect_Input(String cabinetName,
                                             String userName, String password,
                                             String forceful) {
        return
                "<?xml version=\"1.0\"?><WMConnect_Input><Option>WMConnect</Option><UserExist>" +
                forceful + "</UserExist><EngineName>" +
                cabinetName + "</EngineName>\n" +
                "<Particpant>\n" +
                "<Name>" + userName + "</Name>\n" +
                "<Password>" +
                password + "</Password>\n" +
                "<Scope>USER</Scope>\n" +
                "<ParticipantType>U</ParticipantType>\n" +
                "</Participant>\n" + "</WMConnect_Input>";
    }
	
    public static String get_NGOConnectCabinetForceFully_Input(String
            cabinetName, String userName, String password) {
        return "<?xml version=\"1.0\"?>\n<NGOConnectCabinetForceFully_Input>\n<Option>NGOConnectCabinet</Option>\n<UserExist>N</UserExist>\n<CabinetName>" +
                cabinetName + "</CabinetName>\n" + "<UserName>" + userName +
                "</UserName>\n" + "<UserPassword>" + password +
                "</UserPassword>\n" + "</NGOConnectCabinetForceFully_Input>";
    }

    public static String get_WMDisConnect_Input(String cabinetName,
                                                String sessionID) {
        return "<? Xml Version=\"1.0\"?>"
                + "<WMDisConnect_Input>"
                + "<Option>WMDisConnect</Option>"
                + "<EngineName>" + cabinetName +
                "</EngineName>"
                + "<SessionID>" + sessionID +
                "</SessionID>"
                + "</WMDisConnect_Input>";
    }
	public static String get_ODDisConnect_Input(String cabinetName,
                                                String sessionID) {
        return "<?xml version=\"1.0\"?>"
				+"<NGODisconnectCabinet_Input>"
				+"<Option>NGODisconnectCabinet</Option>"
				+"<CabinetName>"+cabinetName+"</CabinetName>"
				+"<UserDBId>"+sessionID+"</UserDBId>"
				+"</NGODisconnectCabinet_Input>";
    }

 

    public static String get_WFGetExternalData_Input(String cabinetName,
            String sessionID, String processDefID, String activityID,
            String WorkItemId,
            String registraionNo) {
        return "<?xml version=\"1.0\"?><WFGetExternalData_Input>\n<Option>WFGetExternalData</Option>\n<EngineName>"
                + cabinetName +
                "</EngineName>\n<SessionId>" + sessionID +
                "</SessionId>\n<ProcessDefinitionID>" + processDefID +
                "</ProcessDefinitionID>\n<ActivityID>"
                + activityID + "</ActivityID>\n<ProcessInstanceId>" +
                registraionNo +
                "</ProcessInstanceId>\n<WorkItemId>" + WorkItemId +
                "</WorkItemId>\n<DefinitionFlag>N</DefinitionFlag>";
    }

    public static String get_WMGetWorkItem_Input(String cabinetName,
                                                 String sessionID,
                                                 String registraionNo,
												 String WID) {
        return "<? Xml Version=\"1.0\"?>\n"
                + "<WMGetWorkItem_Input>\n"
                + "<Option>WMGetWorkItem</Option>\n"
                + "<EngineName>" + cabinetName +
                "</EngineName>\n"
                + "<SessionID>" + sessionID +
                "</SessionID>\n"
                + "<ProcessInstanceId>" + registraionNo +
                "</ProcessInstanceId>\n"
                + "<WorkItemId>"+WID+"</WorkItemId>\n"
                + "</WMGetWorkItem_Input>";
    }

  

   

    public static String get_NGOGetListOfCabinets_Input() {
        return "<?xml version=\"1.0\"?><NGOGetListOfCabinets_Input><Option>NGOGetListOfCabinets</Option></NGOGetListOfCabinets_Input>";
    }

    public static String get_NGOISGetSitesList_Input(String cabinetName) {
        return "<?xml version=\"1.0\"?><NGOISGetSitesList_Input>\n<Option>NGOISGetSitesList</Option>\n<CabinetName>" +
                cabinetName + "</CabinetName>\n</NGOISGetSitesList_Input>";
    }

    public static String get_NGOISGetVolumesList_Input(String cabinetName) {
        return "<?xml version=\"1.0\"?><NGOISGetVolumesList_Input>\n<Option>NGOISGetVolumesList</Option>\n<CabinetName>" +
                cabinetName + "</CabinetName>\n</NGOISGetVolumesList_Input>";
    }


    public static String get_WMConnectCS_Input(String cabinetName,String WFSIP) {
        return "<?xml version=\"1.0\"?>\n"
				+"<WMConnect_Input>\n"
				+"<Option>WMConnect</Option>\n"
				+"<EngineName>" + cabinetName + "</EngineName>\n"
				+"<Name>"+ WFSIP +"</Name>\n"
				+"<ParticipantType>C</ParticipantType>\n"
				+"</WMConnect_Input>";
    }

    public static String get_WMGetProcessList_Input(String cabinetName,
            String sessionID) {
        return "<?xml version=\"1.0\"?><WMGetProcessList_Input>\n<Option>WMGetProcessList</Option>\n<EngineName>" +
                cabinetName + "</EngineName>\n<SessionID>" + sessionID + "</SessionID>\n<DataFlag>N</DataFlag>\n<LatestVersionFlag>N</LatestVersionFlag>\n<OrderBy>2</OrderBy>\n<BatchInfo><NoOfRecordsToFetch>9999</NoOfRecordsToFetch></BatchInfo>\n</WMGetProcessList_Input>";
    }

    public static String get_WMGetActivityList_Input(String cabinetName,
            String sessionID, String processDefID) {
        return "<?xml version=\"1.0\"?><WMGetActivityList_Input>\n<Option>WMGetActivityList</Option>\n<EngineName>" +
                cabinetName + "</EngineName>\n<SessionId>" + sessionID +
                "</SessionId>\n<ProcessDefinitionID>" + processDefID +
                "</ProcessDefinitionID>\n</WMGetActivityList_Input>";
    }

    public static String get_WFGetProcessVariables_Input(String cabinetName,
            String sessionID, String processDefID, String processName,
            String activityID,
            String activityType) {
        return "<?xml version=\"1.0\"?><WFGetProcessVariables_Input>\n<Option>WFGetProcessVariables</Option>\n<EngineName>" +
                cabinetName +
                "</EngineName>\n<SessionID>" + sessionID +
                "</SessionID>\n<ProcessDefinitionId>" +
                processDefID + "</ProcessDefinitionId>\n<ProcessName>" +
                processName +
                "</ProcessName>\n<ActivityId>" + activityID +
                "</ActivityId>\n<ActivityType>" + activityType +
                "</ActivityType>\n</WFGetProcessVariables_Input>";

    }

 

	public static String get_ORACLE_QUERYXML_Input(
			String cabinetName,
			String dsnName,
            String sessionID,
            String sQry) {		

        return "<?xml version=\"1.0\"?>\n"
                + "<APSelect_Input>\n"
                + "<Option>WFExtSelect</Option>\n"
                + "<EngineName>" + cabinetName + "</EngineName>\n"
				+ "<DataSourceName>" + dsnName + "</DataSourceName>\n"
                + "<SessionId>" + sessionID + "</SessionId>\n"
				+ "<Query>" + sQry + "</Query>\n"				
				+ "</APSelect_Input>";
    }
	public static String WMTestSelect(String strQuery,int noofcol,String cabinetName,String sessionID)
	{		
		return "<?xml version=\"1.0\"?>\n"+
			"<WMTestSelect_Input>\n<Option>WFSelectTest_new</Option>\n"+
			"<sQry>"+strQuery+"</sQry>\n"+
			"<EngineName>"+cabinetName+"</EngineName>\n"+
			"<NoOfCols>"+noofcol+"</NoOfCols>\n"+
			"<SessionId>"+sessionID+"</SessionId>\n</WMTestSelect_Input>";
	}

    

  public static String Insert_Table(
			String cabinetName,
            String sessionID,
            String sTable ,
	        String sCols,
	        String sValues
	  ) {
        return "<?xml version=\"1.0\"?>\n"
                + "<WFInsert_new_Input>\n"
                + "<Option>WFInsert_new</Option>\n"
                + "<EngineName>" + cabinetName + "</EngineName>\n"
                + "<SessionId>" + sessionID + "</SessionId>\n"
				+ "<TableName>" + sTable + "</TableName>\n"
				+ "<ColName>" + sCols + "</ColName>\n"
				+ "<Values>" + sValues + "</Values>\n"
				+ "</WFInsert_new_Input>";
    }
	
 public static String Update_Table(
			String cabinetName,
            String sessionID,
            String sTable ,
	        String sCols,
	        String sValues,
		    String sWhereClause

	  ) {
        return "<?xml version=\"1.0\"?>\n"
                + "<WFUpdate_Input>\n"
                + "<Option>WFUpdate_new</Option>\n"
                + "<EngineName>" + cabinetName + "</EngineName>\n"
                + "<SessionId>" + sessionID + "</SessionId>\n"
				+ "<TableName>" + sTable + "</TableName>\n"
				+ "<ColName>" + sCols + "</ColName>\n"
				+ "<Values>" + sValues + "</Values>\n"
				+ "<WhereClause>" + sWhereClause + "</WhereClause>\n"				
				+ "</WFUpdate_Input>";
    }


	 public static String get_WFGetUserQueueDetails_Input(String cabinetName,
            String sessionID, String processDefID, String activityID,
            String userID) {
        return "<?xml version=\"1.0\"?><WFGetUserQueueDetails_Input>\n<Option>WFGetUserQueueDetails</Option>\n<EngineName>" +
                cabinetName + "</EngineName>\n<SessionId>" + sessionID +
                "</SessionId>\n<UserId>" + userID +
                "</UserId>\n<ProcessDefinitionId>" + processDefID +
                "</ProcessDefinitionId>\n<ActivityId>" + activityID +
                "</ActivityId>\n<DataFlag>N</DataFlag>\n</WFGetUserQueueDetails_Input>";
    }

	
	public static String get_WFGetIdforName_Input(String cabinetName,
                                                  String sessionID,
                                                  String processDefID,
                                                  String objectType,
                                                  String objectName) {
        return "<?xml version=\"1.0\"?><WFGetIdforName_Input>\n<Option>WFGetIdforName</Option>\n<EngineName>"
                + cabinetName + "</EngineName>\n<SessionId>" + sessionID +
                "</SessionId>\n<ObjectType>" + objectType +
                "</ObjectType>\n<ObjectName>" + objectName +
                "</ObjectName>\n<ProcessDefID>" + processDefID +
                "</ProcessDefID>\n</WFGetIdforName_Input>";

    } 
	 
 public static String WFOpenInstrumentsList_NextBatch(String strEngineName,
                                               String strWFSSession,
                                               String sQueueID,
                                               String strNoOfRecordToFetch,
											   String strLastProcessInstance) {
        StringBuffer strbuff = new StringBuffer(100);
        strbuff.append(generator.createXMLHeader(1.0F));
        strbuff.append("<WFOpenInstrumentsList_Input>");
        strbuff.append(generator.writeElement("Option", "WMFetchWorkList"));
        strbuff.append(generator.writeElement("EngineName", strEngineName));
        strbuff.append(generator.writeElement("SessionId", strWFSSession));
        strbuff.append(generator.writeElement("CountFlag", "Y"));
        strbuff.append(generator.writeElement("QueueId", sQueueID));
        strbuff.append(generator.writeElement("DataFlag", "Y"));
        strbuff.append("<Filter>");
        strbuff.append(generator.writeElement("Type", "256"));
        strbuff.append("<AttributeName></AttributeName>");
        strbuff.append("<Comparison>0</Comparison>");
        strbuff.append("<FilterString></FilterString>");
        strbuff.append("<Length>0</Length>");
        strbuff.append("</Filter>");
        strbuff.append("<BatchInfo>");
        strbuff.append(generator.writeElement("NoOfRecordsToFetch",strNoOfRecordToFetch));
        strbuff.append(generator.writeElement("OrderBy", "2"));
        strbuff.append(generator.writeElement("SortOrder", "A"));
        strbuff.append(generator.writeElement("LastProcessInstance", strLastProcessInstance));
        strbuff.append(generator.writeElement("LastWorkItem", "1"));
        strbuff.append(generator.writeElement("LastValue", strLastProcessInstance));
        strbuff.append("</BatchInfo>");
        strbuff.append("</WFOpenInstrumentsList_Inpu>");
        return strbuff.toString();
    } 
	
	
	public static String NGOAddDocument_Input(String cabinetName,String sessionID, String ParentFolderIndex,String DocumentName,String VolumeIndex,String DocIDX,short volIdShort,long DocumentSize,String CreatedByAppName,int numOfPages, String versionFlag, String docType) {
    return "?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" 
						+ "<NGOAddDocument_Input>\n" 
						+ "<Option>NGOAddDocument</Option>\n" 
						+ "<CabinetName>" +cabinetName+"</CabinetName>\n" 
						+ "<UserDBId>" +sessionID+ "</UserDBId>\n" 
						+"<ParentFolderIndex>"+ParentFolderIndex+"</ParentFolderIndex>\n" 
						+"<AccessType></AccessType>\n" 
						+"<DocumentName>"+DocumentName+"</DocumentName>\n"
						+"<CreatedByAppName>"+CreatedByAppName+"</CreatedByAppName>\n" 
						+"<VolumeIndex>"+VolumeIndex+"</VolumeIndex>\n"
						+"<ISIndex>"+DocIDX+"#"+volIdShort+"#</ISIndex>"
						+"<VersionFlag>"+versionFlag+"</VersionFlag>\n"
						+"<NoOfPages>"+numOfPages+"</NoOfPages>"+"<DocumentType>"+docType+"</DocumentType>"
						+"<DocumentSize>"+DocumentSize+"</DocumentSize>"
						+"</NGOAddDocument_Input>";
						}
						
	public static String NGODeleteDocumentExt_Input(String cabinetName,String sessionID, String ParentFolderIndex,String DocIndex) {
    return "?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" 
						+ "<NGODeleteDocumentExt_Input>\n" 
						+ "<Option>NGODeleteDocumentExt</Option>\n" 
						+ "<CabinetName>" +cabinetName+"</CabinetName>\n" 
						+ "<UserDBId>" +sessionID+ "</UserDBId>\n" 
						+"<Documents>\n" 
						+"<Document>\n" 
						+"<DocumentIndex>"+DocIndex+"</DocumentIndex>\n"
						+"<ParentFolderIndex>"+ParentFolderIndex+"</ParentFolderIndex>\n" 
						+"<ReferenceFlag>N</ReferenceFlag>\n"
						+"</Document>"
						+"</Documents>\n"
						+"</NGODeleteDocumentExt_Input>";
	}
						
	public static String WMCompleteWorkItem(String strEngineName,
                                            String strSessionId,
                                            String strProcessInstanceId,
                                            String strWorkItemId,
                                            String strAuditStatus) {
        WFInputXml wfInputXml = new WFInputXml();
        wfInputXml.appendStartCallName("WMCompleteWorkItem", "Input");
        wfInputXml.appendTagAndValue("EngineName", strEngineName);
        wfInputXml.appendTagAndValue("SessionId", strSessionId);
        wfInputXml.appendTagAndValue("ProcessInstanceId", strProcessInstanceId);
        wfInputXml.appendTagAndValue("WorkItemId", strWorkItemId);
        wfInputXml.appendTagAndValue("AuditStatus", strAuditStatus);
        wfInputXml.appendEndCallName("WMCompleteWorkItem", "Input");
        return wfInputXml.toString();
    }

	


}


