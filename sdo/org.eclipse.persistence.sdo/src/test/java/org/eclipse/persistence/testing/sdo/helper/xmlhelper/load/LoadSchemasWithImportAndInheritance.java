/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// dmccann - Jan 28/2009 - 1.1 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.load;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import junit.framework.TestCase;

import org.eclipse.persistence.sdo.helper.SDOXSDHelper;
import org.eclipse.persistence.sdo.helper.SchemaResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import commonj.sdo.DataObject;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLHelper;
import commonj.sdo.helper.XSDHelper;
import commonj.sdo.impl.HelperProvider;

public class LoadSchemasWithImportAndInheritance extends TestCase {
    static String PATH = "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/load/";
    SDOXSDHelper xsdHelper = (SDOXSDHelper) XSDHelper.INSTANCE;

    public void testLoadSchemasWithImportAndInheritance() {
        loadXSD(PATH + "SchemaA.xsd");
        loadXSD(PATH + "SchemaB.xsd");
    }

    public void loadXSD(String xsdFileName) {
        FileInputStream fInstream = null;
        try {
            fInstream = new FileInputStream(xsdFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }
        xsdHelper.define(new StreamSource(fInstream), new MySchemaResolver());
    }

    private class MySchemaResolver implements SchemaResolver {
        public Source resolveSchema(Source sourceXSD, String namespace, String schemaLocation) {
            try {
                FileInputStream fInstream = new FileInputStream("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/load/" + schemaLocation);
                return new StreamSource(fInstream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                fail();
            }

            return null;
        }

        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return null;
        }
    }
}
