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
/**
 *  @version $Header: ChangeSummaryRootLoggingOnModifyComplexAtCSLoadAndSaveTestCases.java 23-apr-2007.14:38:52 dmahar Exp $
 *  @author  mfobrien
 *  @since   11.1
 */
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.modify;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOSetting;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.ChangeSummaryRootLoadAndSaveTestCases;

public class ChangeSummaryRootLoggingOnModifyComplexAtCSLoadAndSaveTestCases extends ChangeSummaryRootLoadAndSaveTestCases {
    public ChangeSummaryRootLoggingOnModifyComplexAtCSLoadAndSaveTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.modify.ChangeSummaryRootLoggingOnModifyComplexAtCSLoadAndSaveTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/team_csroot_modify_complex_single.xml");
    }


    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        ChangeSummary teamCS = document.getRootObject().getChangeSummary();
        assertNotNull(teamCS);
        DataObject manager = document.getRootObject().getDataObject("manager");
        assertNotNull(manager);

        assertTrue(teamCS.isLogging());
        assertTrue(((SDOChangeSummary)teamCS).isLogging());

        assertFalse(teamCS.isCreated(manager));
        assertTrue(teamCS.isModified(manager));
        DataObject phone3 = manager.getDataObject("phone[3]");
        assertEquals("3456789", phone3.getString("number"));
        teamCS.isModified(phone3);

        List managersettings = teamCS.getOldValues(manager);

        //assertEquals(1, managersettings.size());        
        SDOSetting nameSetting = (SDOSetting)teamCS.getOldValue(manager, manager.getInstanceProperty("name"));
        assertEquals("Old Name", nameSetting.getValue());

        List settings = teamCS.getOldValues(phone3);
        assertEquals(1, settings.size());
        SDOSetting firstSetting = (SDOSetting)settings.get(0);
        assertEquals("1111111", firstSetting.getValue());
        //setting for number should have value 1111111

        /*        DataObject address = manager.getDataObject("address");
        assertNotNull(address);
        ChangeSummary addressCS = address.getChangeSummary();

        List phones = manager.getList("phone");
        for (int i = 0; i < phones.size(); i++) {
            DataObject nextPhone = ((DataObject)phones.get(i));
            ChangeSummary phoneCS = nextPhone.getChangeSummary();
            // verify changesummary tree integrity
            assertEquals(teamCS, phoneCS);
            // we modified the address
            if (i == 2) {
                assertEquals("3456789",nextPhone.getString("number"));
                assertTrue(teamCS.isCreated(nextPhone));
            } else {
                assertFalse(teamCS.isCreated(nextPhone));
            }
        }

        // verify child change summaries are equal to their parent
        assertEquals(teamCS, addressCS);
        assertTrue(teamCS.isCreated(address));
        */

        // verify logging status
        //assertEquals(0, teamCS.getOldValues(address).size());
        //DataObject addressDO = manager.getDataObject("address");
        //assertTrue(teamCS.isModified(addressDO));
        //modify stuff for write
        // document.getRootObject();
    }
}
