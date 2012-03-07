/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.sdo.model.dataobject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import junit.framework.TestCase;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;

import commonj.sdo.DataObject;

public class NewerContextTestCases extends TestCase {

    private static final String CHILD_ELEMENT_NAME = "child";
    private static final String ROOT_ELEMENT_NAME = "root";
    private static final String NAMESPACE_URI = "http://www.example.org";
    private static final String XML_SCHEMA_V1 = "org/eclipse/persistence/testing/sdo/model/dataobject/schema-v1.xsd";
    private static final String XML_SCHEMA_V2 = "org/eclipse/persistence/testing/sdo/model/dataobject/schema-v2.xsd";

    private SDOHelperContext helperContextV2;
    private DataObject doV2WithChildV1;

    public NewerContextTestCases(String name) {
        super(name);
    }

    @Override
    public void setUp() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        helperContextV2 = new SDOHelperContext();

        // VERSION #1
        InputStream xsdV1 = cl.getResourceAsStream(XML_SCHEMA_V1);
        helperContextV2.getXSDHelper().define(xsdV1, null);
        DataObject doV1 = helperContextV2.getDataFactory().create(NAMESPACE_URI, ROOT_ELEMENT_NAME);
        DataObject childV1 = doV1.createDataObject(CHILD_ELEMENT_NAME);
        assertEquals(1, helperContextV2.getTypeHelper().getType(childV1.getType().getURI(), childV1.getType().getName()).getDeclaredProperties().size());

        // VERSION #2
        helperContextV2.reset();
        InputStream xsdV2 = cl.getResourceAsStream(XML_SCHEMA_V2);
        helperContextV2.getXSDHelper().define(xsdV2, null);
        assertEquals(2, helperContextV2.getTypeHelper().getType(childV1.getType().getURI(), childV1.getType().getName()).getDeclaredProperties().size());
        doV2WithChildV1 = helperContextV2.getDataFactory().create(NAMESPACE_URI, ROOT_ELEMENT_NAME);
        doV2WithChildV1.setDataObject(CHILD_ELEMENT_NAME, doV1.getDataObject(CHILD_ELEMENT_NAME));
    }


    public void testXMLHelperSave() throws Exception {
        helperContextV2.getXMLHelper().save(doV2WithChildV1, NAMESPACE_URI, ROOT_ELEMENT_NAME);
    }

    public void testCopyHelperCopy() throws Exception {
        helperContextV2.getCopyHelper().copy(doV2WithChildV1);
    }

    public void testEqualityHelperEqual() throws Exception {
        helperContextV2.getEqualityHelper().equal(doV2WithChildV1, doV2WithChildV1);
    }

}
