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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;

public class ChangeSummaryRootSimpleCreateManyTestCases extends ChangeSummaryRootLoadAndSaveTestCases {
    public ChangeSummaryRootSimpleCreateManyTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.ChangeSummaryRootSimpleCreateManyTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/team_csroot_create_simple_many.xml");
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

        assertEquals(teamCS, addressCS);
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
        assertFalse(addressSetting.isSet());
        assertNull(addressSetting.getValue());

        //make sure settings has entire phone list        
    }
}
