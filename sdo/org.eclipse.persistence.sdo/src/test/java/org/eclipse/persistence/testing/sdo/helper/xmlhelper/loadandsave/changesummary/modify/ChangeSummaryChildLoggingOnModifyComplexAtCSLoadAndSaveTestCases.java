/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
/**
 *  @version $Header: ChangeSummaryChildLoggingOnModifyComplexAtCSLoadAndSaveTestCases.java 23-apr-2007.14:38:47 dmahar Exp $
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
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.ChangeSummaryChildLoadAndSaveTestCases;

public class ChangeSummaryChildLoggingOnModifyComplexAtCSLoadAndSaveTestCases extends ChangeSummaryChildLoadAndSaveTestCases {
    public ChangeSummaryChildLoggingOnModifyComplexAtCSLoadAndSaveTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.modify.ChangeSummaryChildLoggingOnModifyComplexAtCSLoadAndSaveTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/team_cschild_modify_complex_single.xml");
    }

    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/team_cschild_modify_complex_single_noschema.xml");
    }

    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        ChangeSummary teamCS = document.getRootObject().getChangeSummary();
        assertNull(teamCS);
        DataObject manager = document.getRootObject().getDataObject("manager");
        assertNotNull(manager);
        ChangeSummary managerCS = manager.getChangeSummary();
        assertNotNull(managerCS);
        DataObject address = manager.getDataObject("address");
        ChangeSummary addressCS = address.getChangeSummary();
        assertNotNull(addressCS);
        assertTrue(managerCS.isLogging());
        assertTrue(((SDOChangeSummary)managerCS).isLogging());

        assertEquals(addressCS, managerCS);

        assertFalse(managerCS.isCreated(manager));
        assertTrue(managerCS.isModified(manager));
        DataObject phone3 = manager.getDataObject("phone[3]");
        assertEquals("3456789", phone3.getString("number"));
        managerCS.isModified(phone3);

        List managersettings = managerCS.getOldValues(manager);

        //assertEquals(1, managersettings.size());
        SDOSetting nameSetting = (SDOSetting)managerCS.getOldValue(manager, manager.getInstanceProperty("name"));
        assertEquals("Old Name", nameSetting.getValue());

        List settings = managerCS.getOldValues(phone3);
        assertEquals(1, settings.size());
        SDOSetting firstSetting = (SDOSetting)settings.get(0);
        assertEquals("1111111", firstSetting.getValue());
        assertTrue(firstSetting.isSet());
    }
}
