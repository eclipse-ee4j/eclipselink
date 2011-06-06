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
package org.eclipse.persistence.testing.sdo.helper.typehelper.define;
import commonj.sdo.DataObject;
import commonj.sdo.Type;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDOTypeHelperDefineTwiceTestCases extends SDOTestCase {
    public SDOTypeHelperDefineTwiceTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.typehelper.define.SDOTypeHelperDefineTwiceTestCases" };
        TestRunner.main(arguments);
    }

    public void testDefineTwiceName() {
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);

        DataObject theDO = dataFactory.create(typeType);
        theDO.set("name","theName");
        theDO.set("uri","theUri");
        addProperty(theDO, "prop1", SDOConstants.SDO_STRING, false, false ,true);
        Type newType = typeHelper.define(theDO);
        assertEquals(1, newType.getDeclaredProperties().size());
        
        DataObject theDO2 = dataFactory.create(typeType);
        theDO2.set("name","theName");
        theDO2.set("uri","theUri");
        addProperty(theDO2, "prop2", SDOConstants.SDO_STRING, false, false ,true);
        Type newType2 = typeHelper.define(theDO2);
        assertEquals(1, newType.getDeclaredProperties().size());
        assertEquals("prop1", ((SDOProperty)newType.getDeclaredProperties().get(0)).getName());
        assertEquals(newType, newType2);
    }
    
      public void testDefineTwiceDiffUri() {
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);

        DataObject theDO = dataFactory.create(typeType);
        theDO.set("name","theName");
        theDO.set("uri","theUri");
        Type newType = typeHelper.define(theDO);
      
        DataObject theDO2 = dataFactory.create(typeType);
        theDO2.set("name","theName");
        theDO2.set("uri","theUri2");
        Type newType2 = typeHelper.define(theDO2);
        
        assertFalse(newType == newType2);
    }
}
