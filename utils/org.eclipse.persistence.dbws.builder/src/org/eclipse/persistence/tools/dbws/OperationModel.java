/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - May 01 2008, created DBWS tools package
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

public class OperationModel {

    protected String name;
    protected boolean isSimpleXMLFormat;
    protected String simpleXMLFormatTag;
    protected String xmlTag;
    protected boolean isCollection = false;
    protected boolean binaryAttachment;
    protected String attachmentType;
    protected String returnType;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsSimpleXMLFormat() {
        return isSimpleXMLFormat;
    }
    public void setIsSimpleXMLFormat(boolean isSimpleXMLFormat) {
        this.isSimpleXMLFormat = isSimpleXMLFormat;
    }

    public boolean isSimpleXMLFormat() {
        if (simpleXMLFormatTag != null && simpleXMLFormatTag.length() > 0) {
            isSimpleXMLFormat = true;
        }
        if (xmlTag != null && xmlTag.length() > 0) {
            isSimpleXMLFormat = true;
        }
        return isSimpleXMLFormat;
    }

    public String getSimpleXMLFormatTag() {
        return simpleXMLFormatTag;
    }
    public void setSimpleXMLFormatTag(String simpleXMLFormatTag) {
        this.simpleXMLFormatTag = simpleXMLFormatTag;
        if (simpleXMLFormatTag != null && simpleXMLFormatTag.length() > 0) {
            setIsSimpleXMLFormat(true);
        }
    }

    public String getXmlTag() {
        return xmlTag;
    }
    public void setXmlTag(String xmlTag) {
        this.xmlTag = xmlTag;
        if (xmlTag != null && xmlTag.length() > 0) {
            setIsSimpleXMLFormat(true);
        }
    }

    public boolean isCollection() {
        return isCollection;
    }
    public void setIsCollection(boolean isCollection) {
        this.isCollection = isCollection;
    }

    public boolean getBinaryAttachment() {
        return binaryAttachment;
    }
    public void setBinaryAttachment(boolean binaryAttachment) {
        this.binaryAttachment = binaryAttachment;
    }

    public String getAttachmentType() {
        return attachmentType;
    }
    public void setAttachmentType(String attachmentType) {
        if ("MTOM".equalsIgnoreCase(attachmentType) || "SWAREF".equalsIgnoreCase(attachmentType)) {
            this.attachmentType = attachmentType;
        }
    }

    public String getReturnType() {
        return returnType;
    }
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public boolean isTableOperation() {
        return false;
    }
    public boolean isSQLOperation() {
        return false;
    }

    public boolean isBatchSQLOperation() {
        return false;
    }

    public boolean isProcedureOperation() {
        return false;
    }

    public boolean hasBuildSql() {
        return false;
    }

    public void buildOperation(DBWSBuilder builder) {
        return;
    }
}
