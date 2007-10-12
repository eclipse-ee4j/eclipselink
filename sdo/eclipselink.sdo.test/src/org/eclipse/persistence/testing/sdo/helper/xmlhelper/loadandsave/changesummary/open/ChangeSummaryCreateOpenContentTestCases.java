/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
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

    /*protected String getControlWriteFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/open/team_csroot_create_open_write.xml");
    }

    protected String getNoSchemaControlWriteFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/open/team_csroot_create_open_write.xml");
    }*/

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
        ChangeSummary managerCS = manager.getChangeSummary();
        assertEquals(teamCS, managerCS);
        assertTrue(teamCS.isLogging());
        assertTrue(((SDOChangeSummary)teamCS).isLogging());

        DataObject address = manager.getDataObject("address");
        assertNotNull(address);
        assertCreated(address, teamCS);

        //DataObject yard = manager.getDataObject("theYard");
        //Object yard = manager.get("theYard");
        List  yards = manager.getList("theYard");
        assertEquals(1, yards.size());
        DataObject yard = (DataObject)yards.get(0);
        assertNotNull(yard);
        assertCreated(yard, teamCS);
    }
    
     protected void registerTypes() {
        Type stringType = typeHelper.getType("commonj.sdo", "String");
        Type employeeType = registerEmployeeType();

        // create a new Type for Customers
        DataObject teamType = dataFactory.create("commonj.sdo", "Type");

        SDOProperty prop = (SDOProperty)teamType.getType().getProperty("uri");
        teamType.set(prop, getControlRootURI());

        prop = (SDOProperty)teamType.getType().getProperty("name");
        teamType.set(prop, "Team");
        teamType.set("open", true);
        addProperty(teamType, "name", stringType, true, false, true);
        DataObject managerProp = addProperty(teamType, "manager", employeeType, true, false, true);
        DataObject myChangeSummaryProp = addProperty(teamType, "myChangeSummary", SDOConstants.SDO_CHANGESUMMARY, true, false, true);

        Type teamSDOType = typeHelper.define(teamType);

        /*DataObject propDO = dataFactory.create(SDOConstants.SDO_PROPERTY);
        propDO.set("name", getControlRootName());
        propDO.set("type", teamSDOType);
        typeHelper.defineOpenContentProperty(getControlRootURI(), propDO);*/
    }
}