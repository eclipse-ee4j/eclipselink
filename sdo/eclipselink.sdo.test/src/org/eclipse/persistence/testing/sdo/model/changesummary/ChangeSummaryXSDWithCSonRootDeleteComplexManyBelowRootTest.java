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
package org.eclipse.persistence.testing.sdo.model.changesummary;

import junit.textui.TestRunner;
import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;

public class ChangeSummaryXSDWithCSonRootDeleteComplexManyBelowRootTest extends ChangeSummaryOnRootTestCases {
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/PurchaseOrderDeepWithCSonRootDeleteComplexManyBelowRoot.xml");
    }

    protected String getControlFileName2() {
        return getControlFileName();
    }

    public ChangeSummaryXSDWithCSonRootDeleteComplexManyBelowRootTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryXSDWithCSonRootDeleteComplexManyBelowRootTest" };
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
        assertDeleteDetachUnsetComplexManyBelowRoot(true,//
                                                    true,//
                                                    itemsDO,//
                                                    item2DO,//
                                                    item2ProductDO,//
                                                    item2ProductPrice1DO,//
                                                    item2ProductPrice2DO);
    }

    public void testDeleteComplexManyBelowRoot() throws Exception{
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

        cs.beginLogging();
        item2DO.delete();

        assertDeleteDetachUnsetComplexManyBelowRoot(false,//
                                                    true,//
                                                    itemsDO,//
                                                    item2DO,//
                                                    item2ProductDO,//
                                                    item2ProductPrice1DO,//
                                                    item2ProductPrice2DO);
    }
}
