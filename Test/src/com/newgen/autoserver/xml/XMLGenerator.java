/*------------------------------------------------------------------------------------------------------
												NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group: Application -Projects

Project/Product:  Bandhan AO LOS Process

Application: Bulk Documnet Upload Utility

Module: Main Module

File Name: XMLGenerator.java

Author: Divya Gautam

Date (DD/MM/YYYY): 15/05/2015

Description: Class for generating xml strings.

-------------------------------------------------------------------------------------------------------

CHANGE HISTORY

-------------------------------------------------------------------------------------------------------

Problem No/CR No   Change Date   Changed By    Change Description

------------------------------------------------------------------------------------------------------*/
package com.newgen.autoserver.xml;

public class XMLGenerator {

  public XMLGenerator() {
  }

  public StringBuffer createXMLHeader(float versionNo) {
    StringBuffer pString = new StringBuffer(25);
    pString.append("<?xml version=\"");
    pString.append(versionNo);
    pString.append("\"?>");
    return pString;
  }

  public StringBuffer writeElement(String tag, String value) {
    StringBuffer pString = new StringBuffer();
    if (value != null) {
      pString.append("<");
      pString.append(tag);
      pString.append(">");
      pString.append(value.trim());
      pString.append("</");
      pString.append(tag);
      pString.append(">");
    }
    return pString;
  }
}
