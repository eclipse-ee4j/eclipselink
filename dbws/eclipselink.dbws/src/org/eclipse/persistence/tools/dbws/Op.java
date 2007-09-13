/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

// Javase imports
import java.sql.Connection;

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.internal.oxm.schema.model.Schema;
import org.eclipse.persistence.internal.xr.XRServiceModel;

// Ant imports
import org.apache.tools.ant.Task;

public class Op extends Task {

    protected boolean isSimpleXMLFormat;
    protected String simpleXMLFormatTag;
    protected String xmlTag;
    protected boolean isCollection = true;
    protected boolean binaryAttachment = false;
    protected String returnType;

    public void addTask(Task task) {
    } // ignore

    public boolean getIsSimpleXMLFormat() {
        return isSimpleXMLFormat;
    }
    public void setIsSimpleXMLFormat(boolean isSimpleXMLFormat) {
        this.isSimpleXMLFormat = isSimpleXMLFormat;
    }

    public String getSimpleXMLFormatTag() {
        return simpleXMLFormatTag;
    }
    public void setSimpleXMLFormatTag(String simpleXMLFormatTag) {
        this.simpleXMLFormatTag = simpleXMLFormatTag;
    }

    public String getXmlTag() {
        return xmlTag;
    }
    public void setXmlTag(String xmlTag) {
        this.xmlTag = xmlTag;
    }

    public boolean getIsCollection() {
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

    public String getReturnType() {
        return returnType;
    }
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public void buildOperation(XRServiceModel xrServiceModel, Schema schema,
        DatabasePlatform databasePlatform, Connection connection) {
    }

}
