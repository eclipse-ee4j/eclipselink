/*******************************************************************************
* Copyright (c) 2011 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - May 19/2010 - 1.2.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.externalizable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;

import javax.activation.DataHandler;

import org.eclipse.persistence.oxm.attachment.XMLAttachmentMarshaller;
import org.eclipse.persistence.sdo.helper.DataObjectInputStream;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;

public class BinaryPropertyTestCases extends SDOTestCase {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/sdo/externalizable/binary.xsd";

    public final String SERIALIZATION_FILE_NAME = tempFileDir + "/BinaryProperty.bin";

    public BinaryPropertyTestCases(String name) {
        super(name);
    }

    @Override
    public void setUp() {
        super.setUp();
        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_RESOURCE);
        this.xsdHelper.define(xsd, null);
        ((SDOXMLHelper)this.xmlHelper).getXmlMarshaller().setAttachmentMarshaller(new MyAttachmentMarshaller());
    }

    public void testBinaryProperty() throws Exception {
        DataObject rootDO = this.dataFactory.create("urn:example", "root");
        rootDO.getChangeSummary().beginLogging();
        byte[] controlBytes = new byte[1];
        controlBytes[0] = (byte) 1;
        rootDO.set("binary", controlBytes);
        serialize(rootDO, SERIALIZATION_FILE_NAME);

        DataObject deserializedRootDO = deserialize(SERIALIZATION_FILE_NAME);
        byte[] testBytes = deserializedRootDO.getBytes("binary");
        assertEquals(controlBytes.length, testBytes.length);
        for(int x=0; x<controlBytes.length; x++) {
            assertEquals(controlBytes[x], testBytes[x]);
        }
        ChangeSummary testChangeSummary = deserializedRootDO.getChangeSummary();
        assertTrue(testChangeSummary.isLogging());
        ChangeSummary.Setting testSetting = testChangeSummary.getOldValue(deserializedRootDO,  deserializedRootDO.getType().getProperty("binary"));
        assertFalse(testSetting.isSet());
    }

    @Override
    public void tearDown() throws Exception {
    }

    private void serialize(DataObject anObject, String filename) {
        FileOutputStream aFileOutputStream = null;
        ObjectOutputStream anObjectInputStream = null;
        try {
            aFileOutputStream = new FileOutputStream(filename);
            anObjectInputStream = new ObjectOutputStream(aFileOutputStream);
            anObjectInputStream.writeObject(anObject);
            anObjectInputStream.flush();
            aFileOutputStream.close();
            anObjectInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DataObject deserialize(String filename) {
        FileInputStream aFileInputStream = null;
        DataObjectInputStream aDataObjectInputStream = null;
        DataObject anObject = null;
        try {
            aFileInputStream = new FileInputStream(filename);
            aDataObjectInputStream = new DataObjectInputStream(aFileInputStream, aHelperContext);
            anObject = (DataObject)aDataObjectInputStream.readObject();
            aDataObjectInputStream.close();
            aFileInputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return anObject;
    }

    private static class MyAttachmentMarshaller implements XMLAttachmentMarshaller {

        private static final String FIXED_ID = "FIXED";

        public String addMtomAttachment(DataHandler data, String elementName, String namespace) {
            return FIXED_ID;
        }

        public String addMtomAttachment(byte[] data, int start, int length, String mimeType, String elementName, String namespace) {
            return FIXED_ID;
        }

        public String addSwaRefAttachment(DataHandler data) {
            return FIXED_ID;
        }

        public String addSwaRefAttachment(byte[] data, int start, int length) {
            return FIXED_ID;
        }

        public boolean isXOPPackage() {
            return true;
        }

    }

}