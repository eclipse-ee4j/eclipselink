/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.save;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.StringWriter;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.SDOXMLHelperTestCases;

public abstract class SDOXMLHelperSaveTestCases extends SDOXMLHelperTestCases {
    public SDOXMLHelperSaveTestCases(String name) {
        super(name);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/Customer.xsd";
    }

    protected void compareResultToControl(String result, String controlString) {
        log("Expected:" + controlString);
        log("Actual  :" + result);
        assertEquals(controlString, result);
    }

    protected String getControlString() {
        return getControlString(getControlFileName());
    }

    protected String getControlString(String fileName) {
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return new String(bytes);
        } catch (Exception e) {
            fail("An error occurred loading the control document");
            e.printStackTrace();
            return null;
        }
    }

    abstract protected String getControlFileName();

    protected String getControlRootURI() {
        return CONTROL_ROOT_URI;
    }

    protected String getControlRootName() {
        return CONTROL_ROOT_NAME;
    }

    abstract protected XMLDocument getXMLDocumentToSave();

    protected DataObject getControlDataObject() {
        return getXMLDocumentToSave().getRootObject();
    }

    public void testSaveDataObjectToString() {
        xsdHelper.define(getSchema(getSchemaName()));
        String result = xmlHelper.save(getControlDataObject(), getControlRootURI(), getControlRootName());
        compareResultToControl(result, getControlString());
    }

    public void testSaveDataObjectToOuputStream() {
        xsdHelper.define(getSchema(getSchemaName()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            xmlHelper.save(getControlDataObject(), getControlRootURI(), getControlRootName(), outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred saving the XMLDocument.");
        }

        compareResultToControl(outputStream.toString(), getControlString());
    }

    public void testSaveXMLDocumentToOutputStream() {
        xsdHelper.define(getSchema(getSchemaName()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            xmlHelper.save(getXMLDocumentToSave(), outputStream, null);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred saving the XMLDocument.");
        }

        compareResultToControl(outputStream.toString(), getControlString());
    }

    public void testSaveXMLDocumentToWriter() {
        xsdHelper.define(getSchema(getSchemaName()));
        StringWriter writer = new StringWriter();
        try {
            xmlHelper.save(getXMLDocumentToSave(), writer, null);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred saving the XMLDocument.");
        }

        compareResultToControl(writer.toString(), getControlString());
    }

    public void testLoadAndSaveXMLDocumentToWriter() {
        xsdHelper.define(getSchema(getSchemaName()));
        StringWriter writer = new StringWriter();
        try {
            FileInputStream inputStream = new FileInputStream(getControlFileName());
            XMLDocument xmlDocument = xmlHelper.load(inputStream);
            xmlHelper.save(xmlDocument, writer, null);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred saving the XMLDocument.");
        }

        compareResultToControl(writer.toString(), getControlString());
    }

    public void testLoadNoSchema() {
        registerTypes();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            xmlHelper.save(getXMLDocumentToSave(), outputStream, null);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred saving the XMLDocument.");
        }

        compareResultToControl(outputStream.toString(), getControlString(getNoSchemaControlFileName()));
    }

    protected abstract void registerTypes();

    protected String getNoSchemaControlFileName() {
        return getControlFileName();
    }
}
