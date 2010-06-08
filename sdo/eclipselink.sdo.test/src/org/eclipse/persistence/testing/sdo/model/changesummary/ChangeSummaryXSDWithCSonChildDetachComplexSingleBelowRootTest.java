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
// delete sales/po[1]/items/item[1]
public class ChangeSummaryXSDWithCSonChildDetachComplexSingleBelowRootTest extends ChangeSummaryOnChildTestCases {
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/CorporationWithCSonChildDetachComplexSingleBelowRoot.xml");
    }

    protected String getControlFileName2() {
        return getControlFileName();
    }

    public ChangeSummaryXSDWithCSonChildDetachComplexSingleBelowRootTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryXSDWithCSonChildDetachComplexSingleBelowRootTest" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();// watch setup redundancy
    }

    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        // replace global object with one from xml file (with cs pre-populated)
        rootObject = document.getRootObject();
        salesPO1CS = rootObject.getDataObject("sales/purchaseOrder[1]").getChangeSummary();
        salesPO2CS = rootObject.getDataObject("sales/purchaseOrder[2]").getChangeSummary();
        developmentPO1CS = rootObject.getDataObject("development/purchaseOrder[1]").getChangeSummary();
        developmentPO2CS = null;//rootObject.getDataObject("development/purchaseOrder[2]").getChangeSummary();
        stock1CS = rootObject.getDataObject("stock[1]").getChangeSummary();
        stock2CS = rootObject.getDataObject("stock[2]").getChangeSummary();
        stock3CS = rootObject.getDataObject("stock[3]").getChangeSummary();
        DataObject itemsDO = rootObject.getDataObject("sales/purchaseOrder[1]/items");
        DataObject item1DO = null;//itemsDO.getDataObject("item[1]");        
        DataObject item1ProductDO = null;//item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = null;//item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = null;//item1ProductDO.getDataObject("price[2]");
        

        assertDeleteDetachUnsetComplexSingleBelowRoot(true,//
                                                      false,//
                                                      itemsDO,//
                                                      item1DO,//
                                                      item1ProductDO,//
                                                      item1ProductPrice1DO,//
                                                      item1ProductPrice2DO);
    }

    public void testDetachComplexManySingleBelowRoot() {
        defineTypes();
        // 1. read in model from a populated XML file - via setup()
        // 2. rootObject will be in pre-operation state
        // 3. get references to the original object tree - before operation is performed
        // 4. [perform operation]
        // 5. compare new modified model with original objects - in memory
        DataObject itemsDO = rootObject.getDataObject("sales/purchaseOrder[1]/items");
        DataObject item1DO = itemsDO.getDataObject("item[1]");        
        DataObject item1ProductDO = item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");
        salesPO1CS = rootObject.getDataObject("sales/purchaseOrder[1]").getChangeSummary();
        salesPO2CS = rootObject.getDataObject("sales/purchaseOrder[2]").getChangeSummary();
        developmentPO1CS = rootObject.getDataObject("development/purchaseOrder[1]").getChangeSummary();
        developmentPO2CS = null;//rootObject.getDataObject("development/purchaseOrder[2]").getChangeSummary();
        stock1CS = rootObject.getDataObject("stock[1]").getChangeSummary();
        stock2CS = rootObject.getDataObject("stock[2]").getChangeSummary();
        stock3CS = rootObject.getDataObject("stock[3]").getChangeSummary();
        
        // turn on cs
        salesPO1CS.beginLogging();
        salesPO2CS.beginLogging();            
        developmentPO1CS.beginLogging();
        //developmentPO2CS.beginLogging();
        stock1CS.beginLogging();
        stock2CS.beginLogging();
        stock3CS.beginLogging();            
        item1DO.detach();

        assertDeleteDetachUnsetComplexSingleBelowRoot(false,//
                                                      false,//
                                                      itemsDO,//
                                                      item1DO,//
                                                      item1ProductDO,//
                                                      item1ProductPrice1DO,//
                                                      item1ProductPrice2DO);
    }
}
