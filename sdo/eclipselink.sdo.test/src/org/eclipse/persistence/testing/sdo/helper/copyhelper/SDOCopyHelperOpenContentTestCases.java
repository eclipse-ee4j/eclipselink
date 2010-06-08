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
package org.eclipse.persistence.testing.sdo.helper.copyhelper;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;
import java.io.FileInputStream;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDOCopyHelperOpenContentTestCases extends SDOTestCase {
    protected DataObject rootObject;

    public SDOCopyHelperOpenContentTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(SDOCopyHelperOpenContentTestCases.class);
    }

    public void setUp() {
        super.setUp();

        xsdHelper.define(getSchema(getSchemaName()));
        try {
            FileInputStream inputStream = new FileInputStream(getXMLFileName());
            XMLDocument document = xmlHelper.load(inputStream);
            rootObject = document.getRootObject();
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred during SDOCopyHelperOpenContentTestCases setup.");
        }
    }

    public void testCopyShallow() throws Exception {
        DataObject shallowCopy = copyHelper.copyShallow(rootObject);

        compareOpenContentProperties(shallowCopy, true);
        assertTrue(equalityHelper.equalShallow(rootObject, shallowCopy));
    }

    public void testCopyDeep() throws Exception {
        DataObject deepCopy = copyHelper.copy(rootObject);
        compareOpenContentProperties(deepCopy, false);
        assertTrue(equalityHelper.equal(rootObject, deepCopy));
    }

    protected String getSchemaName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/CustomerOpenContent.xsd";
    }

    protected String getXMLFileName() {
        return "./org/eclipse/persistence/testing/sdo/helper/xmlhelper/simpleElementOpenContent.xml";
    }

    protected void compareOpenContentProperties(DataObject copy, boolean shallow) {
        int ocPropSize = ((SDODataObject)rootObject)._getOpenContentProperties().size();
        assertEquals(ocPropSize, ((SDODataObject)copy)._getOpenContentProperties().size());
        assertEquals(((SDODataObject)rootObject)._getOpenContentPropertiesAttributes().size(), ((SDODataObject)copy)._getOpenContentPropertiesAttributes().size());        
        assertEquals(rootObject.getInstanceProperties().size(), copy.getInstanceProperties().size());
        for (int i = 0; i < ocPropSize; i++) {
            Property next = (Property)((SDODataObject)rootObject)._getOpenContentProperties().get(i);
            boolean originalSet = rootObject.isSet(next);
            Object originalValue = rootObject.get(next);
            boolean copySet = copy.isSet(next);
            Object copyValue = copy.get(next);
            if (shallow) {
                if (next.getType().isDataType()) {
                    assertEquals(originalSet, copySet);
                    if (next.isMany()) {
                        assertEquals(((List)originalValue).size(), ((List)copyValue).size());
                        assertTrue(((List)originalValue).containsAll((List)copyValue));
                    } else {
                        assertEquals(originalValue, copyValue);
                    }
                } else {
                    assertFalse(copySet);
                }
            } else {
                assertEquals(originalSet, copySet);
                if (next.getType().isDataType()) {
                    if (next.isMany()) {
                        assertEquals(((List)originalValue).size(), ((List)copyValue).size());
                        assertTrue(((List)originalValue).containsAll((List)copyValue));
                    } else {
                        assertEquals(originalValue, copyValue);
                    }
                } else {
                    assertFalse(originalValue == copyValue);
                    if (next.isMany()) {
                        assertEquals(((List)originalValue).size(), ((List)copyValue).size());
                    } else {
                        assertTrue(equalityHelper.equal((DataObject)originalValue, (DataObject)copyValue));
                    }
                }
            }
        }
    }
}
