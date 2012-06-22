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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.delete;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;
import java.util.Iterator;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.ChangeSummaryChildLoadAndSaveTestCases;

public class ChangeSummaryChildSimpleDeleteTestCases extends ChangeSummaryChildLoadAndSaveTestCases {
    public ChangeSummaryChildSimpleDeleteTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.delete.ChangeSummaryChildSimpleDeleteTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/team_cschild_delete_simple.xml");
    }

    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/team_cschild_delete_simple_noschema.xml");
    }

    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        ChangeSummary teamCS = document.getRootObject().getChangeSummary();
        assertNull(teamCS);
        DataObject manager = document.getRootObject().getDataObject("manager");
        assertNotNull(manager);
        ChangeSummary managerCS = manager.getChangeSummary();
        assertNotNull(managerCS);
        assertTrue(managerCS.isLogging());
        assertTrue(((SDOChangeSummary)managerCS).isLogging());

        int deletedSize = ((SDOChangeSummary)managerCS).getDeleted().size();
        assertEquals(3, deletedSize);
        assertTrue(managerCS.isModified(manager));

        Iterator iter = ((SDOChangeSummary)managerCS).getDeleted().iterator();
        while (iter.hasNext()) {
            SDODataObject nextDeleted = (SDODataObject)iter.next();
            assertEquals(nextDeleted.getInstanceProperties().size(), ((SDOChangeSummary)managerCS).getOldValues(nextDeleted).size());
        }

        assertEquals(2, ((SDOChangeSummary)managerCS).getOldValues(manager).size());

        List teamOldValues = managerCS.getOldValues(manager);

        //assertEquals(2, teamOldValues.size());
        //  assertTrue(teamOldValues.size() <= 4);
        ChangeSummary.Setting idSetting = managerCS.getOldValue(manager, manager.getInstanceProperty("id"));
        assertNull(idSetting);
        //TODO: uncomment when Node null policy works
        ChangeSummary.Setting nameSetting = managerCS.getOldValue(manager, manager.getInstanceProperty("name"));
        assertNull(nameSetting);
        ChangeSummary.Setting addressSetting = managerCS.getOldValue(manager, manager.getInstanceProperty("address"));
        assertNotNull(addressSetting);
        DataObject addressDO = (DataObject)addressSetting.getValue();
        assertNotNull(addressDO);
        Object streetValue = addressDO.get("street");
        assertEquals("theStreet", streetValue);

        DataObject originalAddress = (DataObject)((SDOChangeSummary)managerCS).getReverseDeletedMap().get(addressDO);

        DataObject oldContainer = managerCS.getOldContainer(originalAddress);
        assertEquals(oldContainer, manager);

        Property oldContainmentProp = managerCS.getOldContainmentProperty(originalAddress);
        assertNotNull(oldContainmentProp);
        assertEquals(oldContainmentProp, manager.getInstanceProperty("address"));

        //TODO: yard stuff
        ChangeSummary.Setting yardOldSetting = managerCS.getOldValue(originalAddress, addressDO.getInstanceProperty("yard"));

        DataObject yardDO = (DataObject)yardOldSetting.getValue();

        assertNotNull(yardDO);

        DataObject originalYard = (DataObject)((SDOChangeSummary)managerCS).getReverseDeletedMap().get(yardDO);

        DataObject yardOldContainer = managerCS.getOldContainer(originalYard);

        assertEquals(yardOldContainer, originalAddress);
        assertNotNull(yardOldContainer);
        Property yardOldContainmentProp = managerCS.getOldContainmentProperty(originalYard);
        assertNotNull(yardOldContainmentProp);
        assertEquals(yardOldContainmentProp, addressDO.getInstanceProperty("yard"));

        managerCS.getOldValues(yardDO);

        ChangeSummary.Setting phoneSetting = managerCS.getOldValue(manager, manager.getInstanceProperty("phone"));
        assertNotNull(phoneSetting);
        assertNotNull(phoneSetting.getValue());
        assertTrue(phoneSetting.getValue() instanceof List);
        assertEquals(4, ((List)phoneSetting.getValue()).size());

        /*teamCS.endLogging();
        teamCS.beginLogging();
        manager.set("name","Suzanne");
        manager.set("id","123");
        */

        //assertTrue(teamCS.isCreated(manager));
        //assertEquals(0, teamCS.getOldValues(manager).size());
    }
}
