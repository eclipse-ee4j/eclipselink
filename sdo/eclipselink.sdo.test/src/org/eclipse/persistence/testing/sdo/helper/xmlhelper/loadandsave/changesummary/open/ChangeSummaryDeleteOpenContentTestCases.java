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
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOChangeSummary;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.ChangeSummaryRootLoadAndSaveTestCases;

public class ChangeSummaryDeleteOpenContentTestCases extends ChangeSummaryRootLoadAndSaveTestCases {
    public ChangeSummaryDeleteOpenContentTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
	    String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.xmlhelper.loadandsave.changesummary.open.ChangeSummaryDeleteOpenContentTestCases" };      
        TestRunner.main(arguments);
    }

    protected String getControlFileName() {
        return ("./org/eclipse/persistence/testing/sdo/helper/xmlhelper/changesummary/open/team_csroot_delete_open.xml");
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


        List yards = manager.getList("theYard");
        assertTrue(yards.isEmpty());
        
        assertEquals(1, ((SDOChangeSummary)managerCS).getModified().size());
        assertEquals(4, ((SDOChangeSummary)managerCS).getDeleted().size());
        DataObject deletedYard = (DataObject)((SDOChangeSummary)managerCS).getDeleted().get(0);
        
        //TODO: what should be deletedYard.get"width" vs. get oldSetting "width
        assertEquals("theSqFootage", deletedYard.get("squarefootage"));
        assertEquals("thelength", deletedYard.get("length"));
        assertEquals("theWidth", deletedYard.get("width"));
        
        //theYardDefined
        DataObject deletedYardDefined = (DataObject)((SDOChangeSummary)managerCS).getDeleted().get(1);
        ChangeSummary.Setting setting = managerCS.getOldValue(deletedYardDefined,deletedYardDefined.getInstanceProperty("squarefootage"));
        assertNotNull(setting);                  
        assertEquals("theSqFootageDefined", setting.getValue());
        
        setting = managerCS.getOldValue(deletedYardDefined,deletedYardDefined.getInstanceProperty("length"));
        assertNotNull(setting);                  
        assertEquals("thelengthDefined", setting.getValue());
        
        setting = managerCS.getOldValue(deletedYardDefined,deletedYardDefined.getInstanceProperty("width"));
        assertNotNull(setting);                  
        assertEquals("theWidthDefined", setting.getValue());
        
        
        //theYardUndefined
        DataObject deletedYardUndefined = (DataObject)((SDOChangeSummary)managerCS).getDeleted().get(2);
        setting = managerCS.getOldValue(deletedYardUndefined,deletedYardUndefined.getInstanceProperty("squarefootage"));
        assertNotNull(setting);        
        assertEquals(1, ((List)setting.getValue()).size());
        assertEquals("theSqFootageUndefined", ((List)setting.getValue()).get(0));
        
        setting = managerCS.getOldValue(deletedYardUndefined,deletedYardUndefined.getInstanceProperty("length"));
        assertNotNull(setting);                          
        assertEquals(1, ((List)setting.getValue()).size());
        assertEquals("thelengthUndefined", ((List)setting.getValue()).get(0));
        
        setting = managerCS.getOldValue(deletedYardUndefined,deletedYardUndefined.getInstanceProperty("width"));
        assertNotNull(setting);                  
        assertEquals(1, ((List)setting.getValue()).size());
        assertEquals("theWidthUndefined", ((List)setting.getValue()).get(0));        
        //
        
        //address
        DataObject address = (DataObject)((SDOChangeSummary)managerCS).getDeleted().get(3);
        setting = managerCS.getOldValue(address,address.getInstanceProperty("street"));
        assertNotNull(setting);                
        assertEquals("theStreet", setting.getValue());
        assertTrue(setting.isSet());        
        
        ChangeSummary.Setting oldNameSetting = managerCS.getOldValue(manager, manager.getInstanceProperty("name"));
        
        List oldValues = managerCS.getOldValues(manager);
        Sequence oldSequence = managerCS.getOldSequence(manager);
                
        assertEquals(4, oldValues.size());
        assertEquals(5, oldSequence.size());
        
        assertEquals("Jane Doe" ,oldSequence.getValue(0));
        assertEquals("name" ,oldSequence.getProperty(0).getName());
        
        DataObject yardValue = (DataObject)oldSequence.getValue(1);
        assertNotNull(yardValue);
        assertEquals("Yard" ,yardValue.getType().getName());
        assertEquals("theYard" ,oldSequence.getProperty(1).getName());
        
        DataObject yardDefinedValue = (DataObject)oldSequence.getValue(2);
        assertEquals("Yard" ,yardDefinedValue.getType().getName());
        assertNotNull(yardDefinedValue);
       // assertEquals("theYardDefined" ,oldSequence.getProperty(2).getName());
        
        SDODataObject yardUnDefinedValue = (SDODataObject)oldSequence.getValue(3);
        assertNotNull(yardUnDefinedValue);
        assertTrue(yardUnDefinedValue.getType().isOpenSequencedType());
        assertEquals("theYardUndefined" ,oldSequence.getProperty(3).getName());
        
        DataObject addressValue = (DataObject)oldSequence.getValue(4);
        assertEquals("Address" ,addressValue.getType().getName());
        assertEquals("address" ,oldSequence.getProperty(4).getName());
        
        
        
        //olsequence is size 2 with name + address
      
        //DataObject yard = manager.getDataObject("theYard");
       /* List yards = manager.getList("theYard");
        assertEquals(1, yards.size());
        DataObject yard = (DataObject)yards.get(0);
        assertNotNull(yard);
        assertEquals("theSqFootage", yard.get("squarefootage"));
        assertEquals("thelength", yard.get("length"));
        assertEquals("theWidth", yard.get("width"));
        */

    }
}
