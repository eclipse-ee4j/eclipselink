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
package org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.open;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.util.ArrayList;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.ChangeSummaryRootLoadAndSaveTestCases;

public class ChangeSummaryModifyOpenContentTestCases extends ChangeSummaryRootLoadAndSaveTestCases {
    public ChangeSummaryModifyOpenContentTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.open.ChangeSummaryModifyOpenContentTestCases" };
        TestRunner.main(arguments);
    }


    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/open/team_csroot_modify_open.xml");
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

        //DataObject yard = manager.getDataObject("theYard");
        List yards = manager.getList("theYard");
        assertEquals(1, yards.size());
        DataObject yard = (DataObject)yards.get(0);
        assertNotNull(yard);
        assertEquals("theSqFootage", yard.get("squarefootage"));
        assertEquals("thelength", yard.get("length"));
        assertEquals("theWidth", yard.get("width"));

        DataObject yardDefined = manager.getDataObject("theYardDefined");
        assertNotNull(yardDefined);
        assertEquals("theSqFootage", yardDefined.get("squarefootage"));
        assertEquals("thelength", yardDefined.get("length"));
        assertEquals("theWidth", yardDefined.get("width"));
        
        DataObject yardUndefined = (DataObject)(manager.getList("theYardUndefined").get(0));
        assertNotNull(yardUndefined);
        assertEquals("theSqFootageUndefined", yardUndefined.getList("squarefootage").get(0));
        assertEquals("thelengthUndefined", yardUndefined.getList("length").get(0));
        assertEquals("theWidthUndefined", yardUndefined.getList("width").get(0));
                
        String controlValue = "15";
        assertEquals(controlValue, manager.getList("simpleOpenTestDefined").get(0));
        
        List simpleOpenList = manager.getList("simpleOpenTest");
        assertEquals(1, simpleOpenList.size());
        assertEquals(controlValue, simpleOpenList.get(0));

        ChangeSummary.Setting simpleOpenTestDefinedSetting = managerCS.getOldValue(manager, manager.getInstanceProperty("simpleOpenTestDefined"));
        assertNotNull(simpleOpenTestDefinedSetting);
        assertEquals("10", ((List)simpleOpenTestDefinedSetting.getValue()).get(0));


        ChangeSummary.Setting simpleSetting = managerCS.getOldValue(manager, manager.getInstanceProperty("simpleOpenTest"));
        assertNotNull(simpleSetting);
        List oldValueList = (List)simpleSetting.getValue();
        List controlList = new ArrayList();
        controlList.add("8");
        controlList.add("10");
        controlList.add("15");
        assertEquals(controlList.size(),oldValueList.size());
        assertTrue(controlList.containsAll(oldValueList));
        assertTrue(oldValueList.containsAll(controlList));
                  
       
                
    }
}
