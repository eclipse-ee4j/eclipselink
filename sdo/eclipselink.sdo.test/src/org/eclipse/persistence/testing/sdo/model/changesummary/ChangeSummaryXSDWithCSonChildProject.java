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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.ValueStore;
import org.eclipse.persistence.sdo.helper.ListWrapper;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;

public class ChangeSummaryXSDWithCSonChildProject extends SDOTestCase {
    protected DataObject rootObject;
    protected DataObject rootObject2;
    protected DataObject rootObjectOriginalCopy;
    protected ChangeSummary salesPO1CS;
    protected ChangeSummary salesPO2CS;
    protected ChangeSummary developmentPO1CS; // n/a on startup
    protected ChangeSummary developmentPO2CS; // n/a on startup
    protected ChangeSummary stock1CS;
    protected ChangeSummary stock2CS;
    protected ChangeSummary stock3CS;
    protected ChangeSummary salesPO1CS2;
    protected ChangeSummary salesPO2CS2;
    protected ChangeSummary developmentPO1CS2; // n/a on startup
    protected ChangeSummary developmentPO2CS2; // n/a on startup
    protected ChangeSummary stock1CS2;
    protected ChangeSummary stock2CS2;
    protected ChangeSummary stock3CS2;
    
    public static final String URINAME = "http://www.example.org";
    public static final String TYPENAME = "corporation";
    
    

    public ChangeSummaryXSDWithCSonChildProject(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();// watch setup redundancy
        //define types from deep with cs 
        try {
            InputStream is = new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeepWithCSonChild.xsd");
            List types = xsdHelper.define(is, null);
            XMLDocument document = xmlHelper.load(new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeepWithCSonChild.xml"));
            rootObject = document.getRootObject();
            salesPO1CS = rootObject.getDataObject("sales/purchaseOrder[1]").getChangeSummary();
            salesPO2CS = rootObject.getDataObject("sales/purchaseOrder[2]").getChangeSummary();
            developmentPO1CS = rootObject.getDataObject("development/purchaseOrder[1]").getChangeSummary();
            developmentPO2CS = null;//rootObject.getDataObject("development/purchaseOrder[2]").getChangeSummary();
            stock1CS = rootObject.getDataObject("stock[1]").getChangeSummary();
            stock2CS = rootObject.getDataObject("stock[2]").getChangeSummary();
            stock3CS = rootObject.getDataObject("stock[3]").getChangeSummary();

            //rootObjectOriginalCopy = copyHelper.copy(rootObject);
            // see bug #5878605: SDO: COPYHELPER.COPY() LOGS CS CHANGES - SHOULD SUSPEND LOGGING DURING COPY
            // turn off logging before deep copy as a workaround
            salesPO1CS.endLogging();
            salesPO2CS.endLogging();            
            developmentPO1CS.endLogging();
            //developmentPO2CS.endLogging();
            stock1CS.endLogging();
            stock2CS.endLogging();
            stock3CS.endLogging();            
            
            rootObject2 = copyHelper.copy(rootObject);
            
            salesPO1CS2 = rootObject2.getDataObject("sales/purchaseOrder[1]").getChangeSummary();
            salesPO2CS2 = rootObject2.getDataObject("sales/purchaseOrder[2]").getChangeSummary();
            developmentPO1CS2 = rootObject.getDataObject("development/purchaseOrder[1]").getChangeSummary();
            developmentPO2CS2 = null;//rootObject.getDataObject("development/purchaseOrder[2]").getChangeSummary();
            stock1CS2 = rootObject2.getDataObject("stock[1]").getChangeSummary();
            stock2CS2 = rootObject2.getDataObject("stock[2]").getChangeSummary();
            stock3CS2 = rootObject2.getDataObject("stock[3]").getChangeSummary();

            // make sure all logs are off
            salesPO1CS.endLogging();
            salesPO2CS.endLogging();            
            developmentPO1CS.endLogging();
            //developmentPO2CS.endLogging();
            stock1CS.endLogging();
            stock2CS.endLogging();
            stock3CS.endLogging();
            
            salesPO1CS2.endLogging();
            salesPO2CS2.endLogging();            
            developmentPO1CS2.endLogging();
            //developmentPO2CS2.endLogging();
            stock1CS2.endLogging();
            stock2CS2.endLogging();
            stock3CS2.endLogging();
            
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the xsd");
        }
    }

    public void writeXML(DataObject anObject) {
        // verify save
        try {
            xmlHelper.save(rootObject, ChangeSummaryOnChildTestCases.URINAME,//
                           ChangeSummaryOnChildTestCases.TYPENAME,//
                           System.out);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
    CS on child (item)
    /ns0:items/ns0:item[2]/ns0:myChangeSummary/ns0:product/ns0:price[2]
    /ns0:items/ns0:item[2]/ns0:myChangeSummary/ns0:product
    /ns0:items/ns0:item[2]/ns0:myChangeSummary - invalid
    cs on root
    /ns0:myChangeSummary/ns0:items/ns0:item[2]
    /ns0:myChangeSummary/ns0:items
    /ns0:myChangeSummary - invalid
    */

    private void assertYardDeleted(DataObject shipToDO, DataObject yardDO, ChangeSummary cs, boolean nullContainer) {
        assertDeleted(yardDO, cs, nullContainer);
        assertEquals(3, cs.getOldValues(yardDO).size());
        Property sfProp = yardDO.getInstanceProperty("squarefootage");
        Property widthProp = yardDO.getInstanceProperty("width");
        Property lengthProp = yardDO.getInstanceProperty("length");
        ChangeSummary.Setting yardSFsetting = (ChangeSummary.Setting)cs.getOldValue(yardDO, sfProp);
        assertEquals(yardSFsetting.getValue(), null);

        //TODO: uncomment this line.  Will fail unless Node Null policy stuff is fixed
        //assertEquals(false, yardSFsetting.isSet());        
        ChangeSummary.Setting yardWidthsetting = (ChangeSummary.Setting)cs.getOldValue(yardDO, widthProp);
        assertEquals("65", yardWidthsetting.getValue());
        assertEquals(true, yardWidthsetting.isSet());

        ChangeSummary.Setting yardLengththsetting = (ChangeSummary.Setting)cs.getOldValue(yardDO, lengthProp);
        assertEquals("45", yardLengththsetting.getValue());
        assertEquals(true, yardLengththsetting.isSet());

        // shipToDO was not deleted, only check yardDO
        assertChildrenUnset(yardDO);
    }

    private void assertYardDetached(DataObject shipToDO, DataObject yardDO, ChangeSummary cs, boolean nullContainer) {
        assertDetached(yardDO, cs, nullContainer);
        assertEquals(3, cs.getOldValues(yardDO).size());
        Property sfProp = yardDO.getInstanceProperty("squarefootage");
        Property widthProp = yardDO.getInstanceProperty("width");
        Property lengthProp = yardDO.getInstanceProperty("length");
        ChangeSummary.Setting yardSFsetting = (ChangeSummary.Setting)cs.getOldValue(yardDO, sfProp);
        assertEquals(yardSFsetting.getValue(), null);

        //TODO: uncomment this line.  Will fail unless Node Null policy stuff is fixed
        //assertEquals(false, yardSFsetting.isSet());        
        ChangeSummary.Setting yardWidthsetting = (ChangeSummary.Setting)cs.getOldValue(yardDO, widthProp);
        assertEquals("65", yardWidthsetting.getValue());
        assertEquals(true, yardWidthsetting.isSet());

        ChangeSummary.Setting yardLengththsetting = (ChangeSummary.Setting)cs.getOldValue(yardDO, lengthProp);
        assertEquals("45", yardLengththsetting.getValue());
        assertEquals(true, yardLengththsetting.isSet());
    }
}
