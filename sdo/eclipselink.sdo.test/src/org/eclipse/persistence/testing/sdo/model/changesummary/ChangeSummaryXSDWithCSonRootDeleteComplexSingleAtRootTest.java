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

public class ChangeSummaryXSDWithCSonRootDeleteComplexSingleAtRootTest extends ChangeSummaryOnRootTestCases {
    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/PurchaseOrderDeepWithCSonRootDeleteComplexSingleAtRoot.xml");
    }

    protected String getControlFileName2() {
        return getControlFileName();
    }

    public ChangeSummaryXSDWithCSonRootDeleteComplexSingleAtRootTest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.changesummary.ChangeSummaryXSDWithCSonRootDeleteComplexSingleAtRootTest" };
        TestRunner.main(arguments);
    }
    
    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        // replace global object with one from xml file (with cs pre-populated)
        rootObject = document.getRootObject();
        cs = rootObject.getChangeSummary();
        DataObject shipToDO = null;//rootObject.getDataObject("shipTo");
        DataObject yardDO = null;//shipToDO.getDataObject("yard");
        Property containmentProp = null;//shipToDO.getContainmentProperty();

        //List phoneList = shipToDO.getList("phone");
        DataObject phone1 = null;//(DataObject)phoneList.get(0);
        DataObject phone2 = null;//(DataObject)phoneList.get(1);

        testCSonRootDeleteComplexSingleAtRoot(true, shipToDO, containmentProp, yardDO, phone1, phone2);
    }

    public void testDeleteComplexSingleBelowRoot() {
        defineTypes();
        // 1. read in model from a populated XML file - via setup()
        // 2. rootObject will be in pre-operation state
        // 3. get references to the original object tree - before operation is performed
        // 4. [perform operation]
        // 5. compare new modified model with original objects - in memory
        DataObject shipToDO = rootObject.getDataObject("shipTo");
        Property containmentProp = shipToDO.getContainmentProperty();
        DataObject yardDO = shipToDO.getDataObject("yard");
        List phoneList = shipToDO.getList("phone");
        DataObject phone1 = (DataObject)phoneList.get(0);
        DataObject phone2 = (DataObject)phoneList.get(1);
        cs.beginLogging();
        shipToDO.delete();

        testCSonRootDeleteComplexSingleAtRoot(false, shipToDO, containmentProp, yardDO, phone1, phone2);
    }

    public void testCSonRootDeleteComplexSingleAtRoot//testDeleteShipTo() 
    (boolean testLoadSave, DataObject shipToDO, Property containmentProp, DataObject yardDO, DataObject phone1, DataObject phone2) {
        defineTypes();
        //DataObject shipToDO = rootObject.getDataObject("shipTo");
        //DataObject yardDO = shipToDO.getDataObject("yard");
        //List phoneList = shipToDO.getList("phone");
        //DataObject phone1 = (DataObject)phoneList.get(0);
        //DataObject phone2 = (DataObject)phoneList.get(1);
        //Property containmentProp = shipToDO.getContainmentProperty();
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainer().size());
        assertEquals(14, ((SDOChangeSummary)cs).getOldContainmentProperty().size());

        if (!testLoadSave) {
            // for dataType verify copy of shipTo has a set child in deleted list and current value is unset
            assertNull(shipToDO.get("street"));
            DataObject oldContainer = ((SDOChangeSummary)cs).getOldContainer(shipToDO);
            Property shipToProp = oldContainer.getInstanceProperty("shipTo");
            ChangeSummary.Setting oldSetting = ((SDOChangeSummary)cs).getOldValue(oldContainer, shipToProp);
            DataObject deepCopyShipTo = (DataObject)oldSetting.getValue();

            assertEquals("123 Maple Street", deepCopyShipTo.get("street"));
            assertModified(rootObject, cs);
            assertNotNull(cs.getOldContainmentProperty(shipToDO));
            assertNotNull(cs.getOldContainer(shipToDO));
            assertEquals(rootObject, cs.getOldContainer(shipToDO));

            assertEquals(shipToDO, cs.getOldContainer(yardDO));
            assertEquals(shipToDO, cs.getOldContainer(phone1));
            assertEquals(shipToDO, cs.getOldContainer(phone2));

            assertEquals(containmentProp, cs.getOldContainmentProperty(shipToDO));
            assertDeleted(shipToDO, cs);
            //assertDeleted(yardDO, cs);
            assertYardDeleted(shipToDO, yardDO, cs, true);
            assertDeleted(phone1, cs);
            assertDeleted(phone2, cs);
        }
        assertEquals(5, cs.getChangedDataObjects().size());
        //assertEquals(1, cs.getOldValues(rootObject).size());
        ChangeSummary.Setting setting = (ChangeSummary.Setting)cs.getOldValues(rootObject).get(0);
        if (!testLoadSave) {
            assertEquals(containmentProp, setting.getProperty());
            DataObject deepCopyShipTo = (DataObject)((SDOChangeSummary)cs).getDeepCopies().get(shipToDO);

            assertEquals(deepCopyShipTo, setting.getValue());
            assertEquals(true, setting.isSet());
            assertEquals(8, cs.getOldValues(shipToDO).size());
            assertEquals(1, cs.getOldValues(phone1).size());
            assertEquals(1, cs.getOldValues(phone2).size());
            ChangeSummary.Setting phone2setting = (ChangeSummary.Setting)cs.getOldValues(phone2).get(0);
            assertEquals("number", phone2setting.getProperty().getName());
            assertEquals("2345678", phone2setting.getValue());
            assertEquals(true, phone2setting.isSet());
        }
    }

    //public void testLoadFromDomSourceWithURIAndOptionsSaveDataObjectToStreamResult() {
    //}
}
