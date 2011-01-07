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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.load;

import commonj.sdo.helper.XMLDocument;
import java.io.FileInputStream;
import java.io.FileReader;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.SDOXMLHelperTestCases;

public abstract class SDOXMLHelperLoadTestCases extends SDOXMLHelperTestCases {
    public SDOXMLHelperLoadTestCases(String name) {
        super(name);
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/Customer.xsd";
    }

    abstract protected String getFileNameToLoad();

    public void testLoadFromString() {
        xsdHelper.define(getSchema(getSchemaName()));
        try {
            FileInputStream inputStream = new FileInputStream(getFileNameToLoad());
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            XMLDocument document = xmlHelper.load(new String(bytes));
            verifyDocument(document);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred");
        }
    }

    public void testLoadFromInputStream() {
        xsdHelper.define(getSchema(getSchemaName()));
        try {
            FileInputStream inputStream = new FileInputStream(getFileNameToLoad());
            XMLDocument document = xmlHelper.load(inputStream);
            verifyDocument(document);

        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred");
        }
    }

    public void testLoadFromInputStreamNoSchema() {
        createTypes();
        try {
            FileInputStream inputStream = new FileInputStream(getFileNameToLoad());
            XMLDocument document = xmlHelper.load(inputStream);
            verifyDocument(document);

        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred");
        }
    }

    public void testLoadFromInputStreamWithURIAndOptions() {
        xsdHelper.define(getSchema(getSchemaName()));
        try {
            FileInputStream inputStream = new FileInputStream(getFileNameToLoad());
            XMLDocument document = xmlHelper.load(inputStream, null, null);
            verifyDocument(document);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred");
        }
    }

    public void testLoadFromFileReaderWithURIAndOptionsStream() {
        xsdHelper.define(getSchema(getSchemaName()));
        try {
            FileReader reader = new FileReader(getFileNameToLoad());

            XMLDocument document = xmlHelper.load(reader, null, null);
            verifyDocument(document);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An exception occurred");
        }
    }

    protected void verifyDocument(XMLDocument xmlDocument) {
        assertNull(xmlDocument.getRootObject().getContainer());
        assertNull(xmlDocument.getRootObject().getContainmentProperty());
    }

    protected abstract void createTypes();
}
