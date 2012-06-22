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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.open;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.ChangeSummaryRootLoadAndSaveTestCases;

public class ChangeSummaryCreateOpenContentTestCases extends ChangeSummaryRootLoadAndSaveTestCases {
    public ChangeSummaryCreateOpenContentTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.open.ChangeSummaryCreateOpenContentTestCases" };
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/open/team_csroot_create_open.xml");
    }

    protected String getNoSchemaControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/open/team_csroot_create_open.xml");
    }

    protected void verifyAfterLoad(XMLDocument document) {
        super.verifyAfterLoad(document);
        
        
        ChangeSummary teamCS = document.getRootObject().getChangeSummary();
        assertNotNull(teamCS);
        DataObject manager = document.getRootObject().getDataObject("manager");
        assertNotNull(manager);
        assertTrue(manager.getType().isSequenced());
        ChangeSummary managerCS = manager.getChangeSummary();
        assertEquals(teamCS, managerCS);
        assertTrue(teamCS.isLogging());
        assertTrue(((SDOChangeSummary)teamCS).isLogging());

        DataObject address = manager.getDataObject("address");
        assertNotNull(address);
        assertCreated(address, teamCS);

        List  yards = manager.getList("theYard");
        assertEquals(1, yards.size());
        DataObject yard = (DataObject)yards.get(0);
        assertNotNull(yard);
        assertCreated(yard, teamCS);
        
        DataObject theYardDefined = manager.getDataObject("theYardDefined");
        //assertEquals(1, yards.size());
        //DataObject yard = (DataObject)yards.get(0);
        assertNotNull(theYardDefined);
        assertCreated(theYardDefined, teamCS);        
        
        List theYardUndefinedList = manager.getList("theYardUndefined");
        assertEquals(1, theYardUndefinedList.size());
        DataObject theYardUndefined = (DataObject)theYardUndefinedList.get(0);                
        assertNotNull(theYardUndefined);
        assertCreated(theYardUndefined, teamCS);
        
        assertNull(xsdHelper.getGlobalProperty("http://www.example.org","theYardUndefined",true));
        assertNull(xsdHelper.getGlobalProperty("http://www.example.org","theYardUndefined",false));
        
    }
}
