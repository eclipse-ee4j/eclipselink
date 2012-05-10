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
package org.eclipse.persistence.testing.sdo.model.changesummary;

import java.util.List;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import junit.textui.TestRunner;
import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;

public class ChangeSummaryXSDWithCSonRootUnsetComplexSingleBelowRootWithComplexManyChildrenTest extends ChangeSummaryOnRootTestCases {
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/PODWithCSonRootUnsetComplexSingleBelowRootWithComplexManyChildren.xml");
    }

    protected String getControlFileName2() {
        return getControlFileName();
    }

    public ChangeSummaryXSDWithCSonRootUnsetComplexSingleBelowRootWithComplexManyChildrenTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryXSDWithCSonRootUnsetComplexSingleBelowRootWithComplexManyChildrenTest" };
        TestRunner.main(arguments);
    }

    
    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        // replace global object with one from xml file (with cs pre-populated)
        rootObject = document.getRootObject();
        cs = rootObject.getChangeSummary();
        DataObject itemsDO = rootObject.getDataObject("items");

        //List items = (List) cs.getOldValue(aRootObject, aRootObject.getInstanceProperty("items"));// aRootObject.getDataObject("items/item[2]");        
        DataObject item2DO = null;//aRootObject.getDataObject("items/item[2]");
        DataObject item2ProductDO = null;//item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = null;//item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = null;//item2ProductDO.getDataObject("price[2]");

        //cs.beginLogging();        
        assertDeleteDetachUnsetComplexSingleBelowRootWithComplexManyChildren(true,//
                                                                             itemsDO,//
                                                                             item2DO,//
                                                                             item2ProductDO,//
                                                                             item2ProductPrice1DO,//
                                                                             item2ProductPrice2DO);
    }

    public void testDeleteComplexManyBelowRoot() {
        defineTypes();
        // 1. read in model from a populated XML file - via setup()        
        // 2. rootObject will be in pre-operation state
        // 3. get references to the original object tree - before operation is performed
        // 4. [perform operation]
        // 5. compare new modified model with original objects - in memory
        DataObject itemsDO = rootObject.getDataObject("items");
        DataObject item2DO = rootObject.getDataObject("items/item[2]");
        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        // test isMany section of unset() - all items including price1 and price2 are unset
        Property containmentProp = item2ProductPrice1DO.getContainmentProperty();
        cs.beginLogging();
        item2ProductDO.unset(containmentProp);

        assertDeleteDetachUnsetComplexSingleBelowRootWithComplexManyChildren(false,//
                                                                             itemsDO,//
                                                                             item2DO,//
                                                                             item2ProductDO,//
                                                                             item2ProductPrice1DO,//
                                                                             item2ProductPrice2DO);
    }

    // Note: unset of a single item in a list is different than a detach in that all the items under
    // property being unset are detached.
    public void assertDeleteDetachUnsetComplexSingleBelowRootWithComplexManyChildren(// testUnsetItem2Price() {
    boolean testLoadSave,//
    DataObject itemsDO,//
    DataObject item2DO,//
    DataObject item2ProductDO,//
    DataObject item2ProductPrice1DO,//
    DataObject item2ProductPrice2DO) {
        if (!testLoadSave) {
            assertModified(item2ProductDO, cs);
            assertDetached(item2ProductPrice1DO, cs);
            assertDetached(item2ProductPrice2DO, cs);
        }

        assertEquals(3, cs.getChangedDataObjects().size());

        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        if (!testLoadSave) {
            ChangeSummary.Setting containmentSetting = (ChangeSummary.Setting)cs.getOldValues(item2ProductDO).get(0);
            assertEquals("price", containmentSetting.getProperty().getName());
            assertTrue(containmentSetting.getValue() instanceof List);
            assertEquals(2, ((List)containmentSetting.getValue()).size());
            assertEquals(true, containmentSetting.isSet());
        }
    }
}
