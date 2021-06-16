/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
