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

import junit.textui.TestRunner;
import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;

/**
 * There are 2 types of tests model and XML
 * both use the same assertion sets and 2 different CS-on-root and CS-on-child XSD schemas.
 * We will not duplicate the full loadandsave tests here, just toString versions
 *
 *  Model:
 *  1. read in model from a populated XML file - via setup()
 *  2. rootObject will be in pre-operation state
 *  3. get references to the original object tree - before operation is performed
 *  4. [perform operation]
 *  -
 *  5. compare new modified model with original objects - in memory
 *  6.
 *
 *
 *  XML:
 *  1. read in modified model from a modified XML file with CS pre-populated - [perform operation]
 *  2. rootObject will be in post-operation state
 *  3. partially compare new modified model with ([original] objects by doing an effective undo or using oldValues) - in memory
 *  4. save model to XML
 *  5. compare generated XML with file system XML
 *
 *  Combined:
 *
 */
public class ChangeSummaryXSDWithCSonRootDetachComplexManyLeafBelowComplexManyBelowRootTest extends ChangeSummaryOnRootTestCases {
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/PODWithCSonRootDetachComplexManyLeafBelowComplexManyBelowRoot.xml");
    }

    protected String getControlFileName2() {
        return getControlFileName();
    }

    public ChangeSummaryXSDWithCSonRootDetachComplexManyLeafBelowComplexManyBelowRootTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryXSDWithCSonRootDetachComplexManyLeafBelowComplexManyBelowRootTest" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();// watch setup redundancy
    }

    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        // replace global object with one from xml file (with cs pre-populated)
        rootObject = document.getRootObject();
        cs = rootObject.getChangeSummary();
        DataObject itemsDO = rootObject.getDataObject("items");
        DataObject item1DO = rootObject.getDataObject("items/item[1]");
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item1ProductDO = item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = null;//item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[1]");// moved from index 2

        assertCSonRootDeleteDetachUnsetComplexManyLeafBelowComplexManyBelowRoot(true,//
                                                                                itemsDO,//
                                                                                item1DO,//
                                                                                item1ProductDO,//
                                                                                item1ProductPrice1DO,//
                                                                                item1ProductPrice2DO,//
                                                                                item2DO,//
                                                                                item2ProductDO,//
                                                                                item2ProductPrice1DO,//
                                                                                item2ProductPrice2DO);
    }

    public void testDetachComplexManySingleBelowRoot() {
        defineTypes();
        // 1. read in model from a populated XML file - via setup()
        // 2. rootObject will be in pre-operation state
        // 3. get references to the original object tree - before operation is performed
        // 4. [perform operation]
        // 5. compare new modified model with original objects - in memory
        DataObject itemsDO = rootObject.getDataObject("items");
        DataObject item1DO = rootObject.getDataObject("items/item[1]");
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item1ProductDO = item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        cs.beginLogging();
        item2ProductPrice1DO.detach();

        assertCSonRootDeleteDetachUnsetComplexManyLeafBelowComplexManyBelowRoot(false,//
                                                                                itemsDO,//
                                                                                item1DO,//
                                                                                item1ProductDO,//
                                                                                item1ProductPrice1DO,//
                                                                                item1ProductPrice2DO,//
                                                                                item2DO,//
                                                                                item2ProductDO,//
                                                                                item2ProductPrice1DO,//
                                                                                item2ProductPrice2DO);
    }
}
