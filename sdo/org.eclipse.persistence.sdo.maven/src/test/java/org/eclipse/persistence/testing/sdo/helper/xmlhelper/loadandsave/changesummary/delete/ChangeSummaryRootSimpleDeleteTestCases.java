/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.ChangeSummaryRootLoadAndSaveTestCases;

public class ChangeSummaryRootSimpleDeleteTestCases extends ChangeSummaryRootLoadAndSaveTestCases {
    public ChangeSummaryRootSimpleDeleteTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.delete.ChangeSummaryRootSimpleDeleteTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/team_csroot_delete_simple.xml");
    }


    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        ChangeSummary teamCS = document.getRootObject().getChangeSummary();
        assertNotNull(teamCS);
        DataObject manager = document.getRootObject().getDataObject("manager");
        assertNotNull(manager);

        DataObject address = manager.getDataObject("address");
        assertNull(address);

        ChangeSummary managerCS = manager.getChangeSummary();
        assertEquals(teamCS, managerCS);
        assertTrue(teamCS.isLogging());
        assertTrue(((SDOChangeSummary)teamCS).isLogging());

        assertEquals(3, ((SDOChangeSummary)teamCS).getDeleted().size());
        assertTrue(teamCS.isModified(manager));

        Iterator iter = ((SDOChangeSummary)managerCS).getDeleted().iterator();
        while (iter.hasNext()) {
            SDODataObject nextDeleted = (SDODataObject)iter.next();
            assertEquals(nextDeleted.getInstanceProperties().size(), ((SDOChangeSummary)managerCS).getOldValues(nextDeleted).size());
        }
        assertEquals(2, ((SDOChangeSummary)managerCS).getOldValues(manager).size());

        List teamOldValues = teamCS.getOldValues(manager);

        assertEquals(2, teamOldValues.size());
        //assertTrue(teamOldValues.size() <= 4);
        ChangeSummary.Setting idSetting = teamCS.getOldValue(manager, manager.getInstanceProperty("id"));
        assertNull(idSetting);
        //TODO: uncomment when Node null policy works
        ChangeSummary.Setting nameSetting = teamCS.getOldValue(manager, manager.getInstanceProperty("name"));

        assertNull(nameSetting);
        ChangeSummary.Setting addressSetting = teamCS.getOldValue(manager, manager.getInstanceProperty("address"));
        assertNotNull(addressSetting);
        DataObject addressDO = (DataObject)addressSetting.getValue();
        assertNotNull(addressDO);
        Object streetValue = addressDO.get("street");
        assertEquals("theStreet", streetValue);

        DataObject addressOriginal = (DataObject)((SDOChangeSummary)teamCS).getReverseDeletedMap().get(addressDO);
        DataObject oldContainer = teamCS.getOldContainer(addressOriginal);
        assertEquals(manager, oldContainer);

        Property oldContainmentProp = teamCS.getOldContainmentProperty(addressOriginal);
        assertNotNull(oldContainmentProp);
        assertEquals(oldContainmentProp, manager.getInstanceProperty("address"));

        //TODO: yard stuff
        ChangeSummary.Setting yardOldSetting = teamCS.getOldValue(addressOriginal, addressDO.getInstanceProperty("yard"));

        DataObject yardDO = (DataObject)yardOldSetting.getValue();

        assertNotNull(yardDO);
        DataObject yardOriginal = (DataObject)((SDOChangeSummary)teamCS).getReverseDeletedMap().get(yardDO);
        DataObject yardOldContainer = teamCS.getOldContainer(yardOriginal);

        assertEquals(yardOldContainer, addressOriginal);
        assertNotNull(yardOldContainer);
        Property yardOldContainmentProp = teamCS.getOldContainmentProperty(yardOriginal);
        assertNotNull(yardOldContainmentProp);
        assertEquals(yardOldContainmentProp, addressDO.getInstanceProperty("yard"));

        teamCS.getOldValues(yardDO);
        ChangeSummary.Setting squarefootageSetting = teamCS.getOldValue(yardOriginal, yardDO.getInstanceProperty("squarefootage"));
        assertEquals("10000", squarefootageSetting.getValue());

        ChangeSummary.Setting phoneSetting = teamCS.getOldValue(manager, manager.getInstanceProperty("phone"));
        assertNotNull(phoneSetting);
        assertNotNull(phoneSetting.getValue());
        assertTrue(phoneSetting.getValue() instanceof List);
        assertEquals(4, ((List)phoneSetting.getValue()).size());

        //assertTrue(teamCS.isCreated(manager));
        //assertEquals(0, teamCS.getOldValues(manager).size());
    }
}
