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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.delete;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.ChangeSummaryRootLoadAndSaveTestCases;

public class ChangeSummaryRootDetachAndDeleteTestCases extends ChangeSummaryRootLoadAndSaveTestCases {
    public ChangeSummaryRootDetachAndDeleteTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.delete.ChangeSummaryRootDetachAndDeleteTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/team_csroot_detach_delete_simple.xml");
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

        assertEquals(3, ((SDOChangeSummary)teamCS).getDeleted().size());//phone, address        
        assertTrue(teamCS.isModified(manager));

        List teamOldValues = teamCS.getOldValues(manager);
        assertEquals(2, teamOldValues.size());

        ChangeSummary.Setting idSetting = teamCS.getOldValue(manager, manager.getInstanceProperty("id"));
        assertNull(idSetting);
        //assertNull(idSetting.getValue());
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
        assertEquals(oldContainer, manager);

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

        /*teamCS.endLogging();
        teamCS.beginLogging();
        manager.set("name","Suzanne");
        manager.set("id","123");
        */

        //assertTrue(teamCS.isCreated(manager));
        //assertEquals(0, teamCS.getOldValues(manager).size());
    }
}
