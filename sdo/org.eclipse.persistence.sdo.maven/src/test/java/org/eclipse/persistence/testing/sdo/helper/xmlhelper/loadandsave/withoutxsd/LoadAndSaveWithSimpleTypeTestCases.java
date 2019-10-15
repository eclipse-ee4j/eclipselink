/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.withoutxsd;

import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.delegates.SDOXMLHelperDelegate;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;

public class LoadAndSaveWithSimpleTypeTestCases extends SDOTestCase {

    public LoadAndSaveWithSimpleTypeTestCases(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public void testLoadFromStringSaveDocumentToWriter() throws Exception {
        FileInputStream inputStream = new FileInputStream(getControlFileName());
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        DataObject optionsDataObject = dataFactory.create(SDOConstants.ORACLE_SDO_URL, SDOConstants.XMLHELPER_LOAD_OPTIONS);
        optionsDataObject.set(SDOConstants.TYPE_LOAD_OPTION, SDOConstants.SDO_INTEGER);

        XMLDocument document = xmlHelper.load(new StringReader(new String(bytes)), "", optionsDataObject);

        verifyAfterLoad(document);
        StringWriter writer = new StringWriter();
        xmlHelper.save(document, writer, null);
        compareXML(getControlWriteFileName(), writer.toString());

    }

    private String getControlWriteFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/bigint_write.xml";
    }

    private void verifyAfterLoad(XMLDocument document) {
        DataObject obj = document.getRootObject();
        Object value = obj.get(0);
        assertTrue(value instanceof java.math.BigInteger);
    }

    private String getControlFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/loadandsave/bigint.xml";
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

    private String getControlString(String controlFileName) {
        try {
            FileInputStream inputStream = new FileInputStream(controlFileName);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the control document");
            return null;
        }
    }

}
