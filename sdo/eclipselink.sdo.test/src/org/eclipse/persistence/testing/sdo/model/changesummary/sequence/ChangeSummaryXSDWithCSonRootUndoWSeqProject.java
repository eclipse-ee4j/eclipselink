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
package org.eclipse.persistence.testing.sdo.model.changesummary.sequence;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryOnRootTestCases;
import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;

public class ChangeSummaryXSDWithCSonRootUndoWSeqProject extends SDOTestCase {
    protected DataObject rootObject;
    protected DataObject rootObjectOriginalCopy;
    protected ChangeSummary cs;

    public ChangeSummaryXSDWithCSonRootUndoWSeqProject(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();// watch setup redundancy
        //define types from deep with cs 
        try {
            InputStream is = new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeepWithCSWSequence.xsd");
            List types = xsdHelper.define(is, null);
            XMLDocument document = xmlHelper.load(new FileInputStream("org/eclipse/persistence/testing/sdo/helper/xmlhelper/PurchaseOrderDeepWithCSWSequence.xml"));
            rootObject = document.getRootObject();
            rootObjectOriginalCopy = copyHelper.copy(rootObject);
            cs = rootObject.getChangeSummary();
            cs.endLogging();
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred loading the xsd");
        }
    }

    public void writeXML(DataObject anObject) {
        // verify save
        try {
            xmlHelper.save(rootObject, ChangeSummaryOnRootTestCases.URINAME,//
                           ChangeSummaryOnRootTestCases.TYPENAME,//
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
        ChangeSummary.Setting yardSFsetting = cs.getOldValue(yardDO, sfProp);
        assertEquals(yardSFsetting.getValue(), null);

        //TODO: uncomment this line.  Will fail unless Node Null policy stuff is fixed
        //assertEquals(false, yardSFsetting.isSet());        
        ChangeSummary.Setting yardWidthsetting = cs.getOldValue(yardDO, widthProp);
        assertEquals("65", yardWidthsetting.getValue());
        assertEquals(true, yardWidthsetting.isSet());

        ChangeSummary.Setting yardLengththsetting = cs.getOldValue(yardDO, lengthProp);
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
        ChangeSummary.Setting yardSFsetting = cs.getOldValue(yardDO, sfProp);
        assertEquals(yardSFsetting.getValue(), null);

        //TODO: uncomment this line.  Will fail unless Node Null policy stuff is fixed
        //assertEquals(false, yardSFsetting.isSet());        
        ChangeSummary.Setting yardWidthsetting =  cs.getOldValue(yardDO, widthProp);
        assertEquals("65", yardWidthsetting.getValue());
        assertEquals(true, yardWidthsetting.isSet());

        ChangeSummary.Setting yardLengththsetting =  cs.getOldValue(yardDO, lengthProp);
        assertEquals("45", yardLengththsetting.getValue());
        assertEquals(true, yardLengththsetting.isSet());
    }
}
