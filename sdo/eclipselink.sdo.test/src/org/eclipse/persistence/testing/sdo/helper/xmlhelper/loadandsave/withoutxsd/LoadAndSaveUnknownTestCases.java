/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.withoutxsd;

import commonj.sdo.helper.XMLDocument;
import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public abstract class LoadAndSaveUnknownTestCases extends SDOTestCase {
    abstract String getControlFileName();

    abstract String getSchemaName();

    public LoadAndSaveUnknownTestCases(String name) {
        super(name);
    }

    protected String getControlWriteFileName() {
        return getControlFileName();
    }

    protected List defineTypes() {
        if (getSchemaName() == null) {
            return null;
        }
        return xsdHelper.define(getSchema(getSchemaName()));
    }

    public void testLoadFromStringSaveDocumentToWriter() throws Exception {
        List types = defineTypes();

        FileInputStream inputStream = new FileInputStream(getControlFileName());
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        XMLDocument document = xmlHelper.load(new String(bytes));

        verifyAfterLoad(document);
        StringWriter writer = new StringWriter();
        xmlHelper.save(document, writer, null);
        compareXML(getControlWriteFileName(), writer.toString());

    }

    protected void compareXML(String controlFileName, String testString) throws Exception {
        compareXML(controlFileName, testString, true);
    }

    protected void compareXML(String controlFileName, String testString, boolean compareNodes) throws Exception {
        String controlString = getControlString(controlFileName);
        log("Expected:" + controlString);
        log("Actual  :" + testString);

        StringReader reader = new StringReader(testString);
        InputSource inputSource = new InputSource(reader);
        Document testDocument = parser.parse(inputSource);
        reader.close();

        if (compareNodes) {
            assertXMLIdentical(getDocument(controlFileName), testDocument);
        }
    }

    protected String getControlString(String fileName) {
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the control document");
            return null;
        }
    }

    protected void verifyAfterLoad(XMLDocument document) {
        assertNull(document.getRootObject().getContainer());
    }
}
