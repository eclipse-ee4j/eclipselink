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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.create;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.ChangeSummaryRootLoadAndSaveTestCases;

public class ChangeSummaryRootCreateAlreadySetTestCases extends ChangeSummaryRootLoadAndSaveTestCases {
    public ChangeSummaryRootCreateAlreadySetTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.create.ChangeSummaryRootCreateAlreadySetTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/team_csroot_create_simple_many_already_set.xml");
    }

    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        ChangeSummary teamCS = document.getRootObject().getChangeSummary();
        assertNotNull(teamCS);
        DataObject manager = document.getRootObject().getDataObject("manager");
        assertNotNull(manager);
        DataObject address = manager.getDataObject("address");
        assertNotNull(address);
        String theStreet = address.getString("street");
        assertEquals("theStreet", theStreet);
        ChangeSummary addressCS = address.getChangeSummary();
        assertEquals(teamCS, addressCS);

        List phones = manager.getList("phone");
        assertEquals(4, phones.size());
        for (int i = 0; i < phones.size(); i++) {
            DataObject nextPhone = ((DataObject)phones.get(i));
            ChangeSummary phoneCS = nextPhone.getChangeSummary();
            assertEquals(teamCS, phoneCS);
            if (i == 2) {
                assertEquals("3456789", nextPhone.getString("number"));
                assertTrue(teamCS.isCreated(nextPhone));
            } else {
                assertFalse(teamCS.isCreated(nextPhone));
            }
        }

        assertTrue(teamCS.isLogging());
        assertTrue(((SDOChangeSummary)teamCS).isLogging());
        assertTrue(teamCS.isCreated(address));
        assertFalse(teamCS.isCreated(manager));
        assertEquals(0, teamCS.getOldValues(address).size());

        assertTrue(teamCS.isModified(manager));
        List managerSettings = teamCS.getOldValues(manager);

        //TODO:should be 1 will be more under node null policy fixed
        // assertEquals(1, managerSettings.size());
        Property prop = manager.getInstanceProperty("phone");
        ChangeSummary.Setting phoneSetting = teamCS.getOldValue(manager, prop);
        assertTrue(phoneSetting.isSet());
        assertTrue(phoneSetting.getValue() instanceof List);
        assertEquals(3, ((List)phoneSetting.getValue()).size());

        Property addressprop = manager.getInstanceProperty("address");
        ChangeSummary.Setting addressSetting = teamCS.getOldValue(manager, addressprop);
        assertTrue(addressSetting.isSet());
        assertNotNull(addressSetting.getValue());
        String oldStreet = ((SDODataObject)addressSetting.getValue()).getString("street");
        assertEquals("oldStreet", oldStreet);

        //assertDeleted((SDODataObject)addressSetting.getValue(), teamCS);
        //assertDetached((SDODataObject)addressSetting.getValue(), teamCS);
        //List oldValues = teamCS.getOldValues((SDODataObject)addressSetting.getValue());
        //assertEquals(((SDODataObject)addressSetting.getValue()).getInstanceProperties().size(),  oldValues.size());
        Property streetProp = ((SDODataObject)addressSetting.getValue()).getInstanceProperty("street");

        /*ChangeSummary.Setting oldValue = teamCS.getOldValue((SDODataObject)addressSetting.getValue(), streetProp);
        assertNotNull(oldValue);
        assertEquals("oldStreet",oldValue.getValue());
        assertTrue(oldValue.isSet());
        */
        DataObject oldContainer = teamCS.getOldContainer(address);
        assertNull(oldContainer);

        //assertNotNull(teamCS.getOldContainer((SDODataObject)addressSetting.getValue()));
        //  assertEquals(manager,teamCS.getOldContainer((SDODataObject)addressSetting.getValue()));
    }
}
