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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;

public class ChangeSummaryChildCreateSimpleNestedManyTestCases extends ChangeSummaryChildLoadAndSaveTestCases {
    public ChangeSummaryChildCreateSimpleNestedManyTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.ChangeSummaryChildCreateSimpleNestedManyTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/team_cschild_create_simple.xml");
    }

    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/team_cschild_create_simple_noschema.xml");
    }

    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        ChangeSummary teamCS = document.getRootObject().getChangeSummary();
        assertNull(teamCS);
        DataObject manager = document.getRootObject().getDataObject("manager");
        assertNotNull(manager);
        ChangeSummary managerCS = manager.getChangeSummary();
        List phones = manager.getList("phone");
        for (int i = 0; i < phones.size(); i++) {
            DataObject nextPhone = ((DataObject)phones.get(i));
            ChangeSummary phoneCS = nextPhone.getChangeSummary();
            assertEquals(managerCS, phoneCS);
            if (i == 2) {
                assertEquals("3456789", nextPhone.getString("number"));
                assertTrue(managerCS.isCreated(nextPhone));
            } else {
                assertFalse(managerCS.isCreated(nextPhone));
            }
        }

        DataObject address = manager.getDataObject("address");
        assertNotNull(address);
        ChangeSummary addressCS = address.getChangeSummary();
        assertEquals(addressCS, managerCS);
        assertTrue(managerCS.isLogging());
        assertTrue(((SDOChangeSummary)managerCS).isLogging());
        assertTrue(managerCS.isCreated(address));
        assertEquals(0, managerCS.getOldValues(address).size());
    }
}
