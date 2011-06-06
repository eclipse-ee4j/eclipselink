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

import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.ValueStore;
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
public class ChangeSummaryXSDWithCSonChildDeleteChainToComplexSingleAtRootTest extends ChangeSummaryOnChildTestCases {
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/CorporationWithCSonChildDeleteChainToComplexSingleAtRoot.xml");
    }
    
    protected String getNoSchemaControlFileName(){
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/CorporationWithCSonChildDeleteChainToComplexSingleAtRoot.xml");
    }

    protected String getControlFileName2() {
        return getControlFileName();
    }

    public ChangeSummaryXSDWithCSonChildDeleteChainToComplexSingleAtRootTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryXSDWithCSonChildDeleteChainToComplexSingleAtRootTest" };
        TestRunner.main(arguments);
    }
        
    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        // replace global object with one from xml file (with cs pre-populated)
        rootObject = document.getRootObject();
        DataObject po1DO = rootObject.getDataObject("sales/purchaseOrder[1]");    	
        DataObject itemsDO = null;//po1DO.getDataObject("items");
        salesPO1CS = po1DO.getChangeSummary();
        salesPO2CS = rootObject.getDataObject("sales/purchaseOrder[2]").getChangeSummary();
        developmentPO1CS = rootObject.getDataObject("development/purchaseOrder[1]").getChangeSummary();
        developmentPO2CS = null;//rootObject.getDataObject("development/purchaseOrder[2]").getChangeSummary();
        stock1CS = rootObject.getDataObject("stock[1]").getChangeSummary();
        stock2CS = rootObject.getDataObject("stock[2]").getChangeSummary();
        stock3CS = rootObject.getDataObject("stock[3]").getChangeSummary();
        DataObject item1DO = null;//itemsDO.getDataObject("item[1]");
        DataObject item2DO = null;//itemsDO.getDataObject("item[1]");
        DataObject item1ProductDO = null;//item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = null;//item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = null;//item1ProductDO.getDataObject("price[2]");
        

        assertDeleteDetachUnsetComplexSingleAtRoot(true,//
                                                      true,//
                                                      po1DO,//
                                                      itemsDO,//
                                                      item1DO,//
                                                      item2DO,//
                                                      item1ProductDO,//
                                                      item1ProductPrice1DO,//
                                                      item1ProductPrice2DO);
    }

    public void testDeleteComplexManySingleBelowRoot() {
        defineTypes();
        // 1. read in model from a populated XML file - via setup()
        // 2. rootObject will be in pre-operation state
        // 3. get references to the original object tree - before operation is performed
        // 4. [perform operation]
        // 5. compare new modified model with original objects - in memory
        DataObject po1DO = rootObject.getDataObject("sales/purchaseOrder[1]");    	
        DataObject itemsDO = po1DO.getDataObject("items");
        DataObject item1DO = itemsDO.getDataObject("item[1]");        
        DataObject item2DO = itemsDO.getDataObject("item[2]");        
        DataObject item1ProductDO = item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");
        salesPO1CS = po1DO.getChangeSummary();
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
        // start deleting at the bottom of the tree and move up
        item1ProductPrice2DO.delete();
        item1ProductDO.delete(); // ListWrapper.clear() will call copyElements() twice for this parent of many
        item1DO.delete();
        itemsDO.delete(); // ListWrapper.clear() will call copyElements() twice for this parent of many
        boolean wasDeleted = true;
        //writeXML(rootObject, URINAME, TYPENAME, System.out);
        assertDeleteDetachUnsetComplexSingleAtRoot(false,//
        											 wasDeleted,//
        											  po1DO,//
                                                      itemsDO,//
                                                      item1DO,//
                                                      item2DO,//
                                                      item1ProductDO,//
                                                      item1ProductPrice1DO,//
                                                      item1ProductPrice2DO);
    }
/*    
    public void testDeleteChainBottomToRoot_ItemsItem2ProductPrice2_MultiOperation_DeleteComplexManyOfComplexSingleOfComplexManyOfComplexSingleInSequence() {
        // save original root for later comparison after undo
        DataObject originalRootDO = copyHelper.copy(rootObject);
        assertTrue(equalityHelper.equal(rootObject, originalRootDO));

        DataObject itemsDO = rootObject.getDataObject("items");
        DataObject item1DO = rootObject.getDataObject("items/item[1]");
        DataObject item2DO = rootObject.getDataObject("items/item[2]");

        DataObject item1ProductDO = item1DO.getDataObject("product");
        DataObject item1ProductPrice1DO = item1ProductDO.getDataObject("price[1]");
        DataObject item1ProductPrice2DO = item1ProductDO.getDataObject("price[2]");

        DataObject item2ProductDO = item2DO.getDataObject("product");
        DataObject item2ProductPrice1DO = item2ProductDO.getDataObject("price[1]");
        DataObject item2ProductPrice2DO = item2ProductDO.getDataObject("price[2]");

        salesPO1CS.beginLogging();
        // verify original VS is null and save a copy of current VS for object identity testing after undo
        assertValueStoresInitializedAfterLoggingOn(rootObject);
        // save a copy of current VS for object identity testing after undo
        ValueStore aCurrentValueStoreAfterLoggingFirstOn = ((SDODataObject)rootObject).getCurrentValueStore();

        assertNotNull(itemsDO.getChangeSummary());        

        // start deleting at the bottom of the tree and move up
        item2ProductPrice2DO.delete();
        item2ProductDO.delete(); // ListWrapper.clear() will call copyElements() twice for this parent of many
        item2DO.delete();
        itemsDO.delete(); // ListWrapper.clear() will call copyElements() twice for this parent of many
        boolean wasDeleted = true;

        // verify salesPO1CS is null on removed trees
        assertChangeSummaryStatusIfClearedIfCSIsAncestor(itemsDO, true);
        
        // check valueStores
        assertValueStoresCopiedAndSwappedAfterFirstModifyOperation(rootObject, aCurrentValueStoreAfterLoggingFirstOn);        

        assertNotNull(item1DO);
        assertNotNull(item2DO);

        assertModified(rootObject, salesPO1CS);
        assertDeleted(itemsDO, salesPO1CS, wasDeleted);
        assertDeleted(item1DO, salesPO1CS, wasDeleted);
        assertDeleted(item2DO, salesPO1CS, wasDeleted);
        assertDeleted(item1ProductDO, salesPO1CS, wasDeleted);
        assertDeleted(item1ProductPrice1DO, salesPO1CS, wasDeleted);
        assertDeleted(item1ProductPrice2DO, salesPO1CS, wasDeleted);
        assertDeleted(item2ProductDO, salesPO1CS, wasDeleted);
        assertDeleted(item2ProductPrice1DO, salesPO1CS, wasDeleted);
        assertDeleted(item2ProductPrice2DO, salesPO1CS, wasDeleted);
        assertEquals(10, salesPO1CS.getChangedDataObjects().size());
        assertEquals(14, ((SDOChangeSummary)salesPO1CS).getOldContainer().size());//  from 9
        assertEquals(14, ((SDOChangeSummary)salesPO1CS).getOldContainmentProperty().size());// from 9

        // set the items back  (in effect doing an undo)
        //rootObject.set("items", itemsDO);
        
        assertUndoChangesEqualToOriginal(salesPO1CS, rootObject, originalRootDO);

        // verify that property is reset
        assertTrue(rootObject.isSet("items"));
        // get back items object
        DataObject itemsDOundone = rootObject.getDataObject("items");
        // compare with original
        assertTrue(equalityHelper.equal(itemsDOundone, itemsDO));

        assertValueStoresReturnedToStartStateAfterUndoChanges(rootObject, aCurrentValueStoreAfterLoggingFirstOn);
    	
    }
*/     
}
