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
package org.eclipse.persistence.testing.sdo.model.sequence;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDOSequenceListTestCases extends SDOTestCase {
    DataObject rootDataObject;

    public SDOSequenceListTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);
        DataObject typeDO = dataFactory.create(typeType);
        typeDO.set("sequenced", true);
        typeDO.set("name", "rootType");
        addProperty(typeDO, "letters", SDOConstants.SDO_STRING, false, true, true);
        addProperty(typeDO, "numbers", SDOConstants.SDO_STRING, false, true, true);
        Type type = typeHelper.define(typeDO);

        rootDataObject = dataFactory.create(type);

    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.sequence.SDOSequenceListTestCases" };
        TestRunner.main(arguments);
    }

    public void testAddIndex() {
        Property lettersProp = rootDataObject.getInstanceProperty("letters");
        Property numbersProp = rootDataObject.getInstanceProperty("numbers");

        rootDataObject.getSequence().add(lettersProp, "A");
        rootDataObject.getSequence().add(numbersProp, "1");
        rootDataObject.getSequence().add(lettersProp, "B");
        rootDataObject.getSequence().add(numbersProp, "2");
        rootDataObject.getSequence().add(lettersProp, "C");
        rootDataObject.getSequence().add(numbersProp, "3");

        rootDataObject.getSequence().add(2, lettersProp, "D");

        assertEquals("A", rootDataObject.getSequence().getValue(0));
        assertEquals("1", rootDataObject.getSequence().getValue(1));
        assertEquals("D", rootDataObject.getSequence().getValue(2));
        assertEquals("B", rootDataObject.getSequence().getValue(3));
        assertEquals("2", rootDataObject.getSequence().getValue(4));
        assertEquals("C", rootDataObject.getSequence().getValue(5));
        assertEquals("3", rootDataObject.getSequence().getValue(6));

        List lettersListValue = rootDataObject.getList("letters");
        assertEquals("A", lettersListValue.get(0));
        assertEquals("D", lettersListValue.get(1));
        assertEquals("B", lettersListValue.get(2));
        assertEquals("C", lettersListValue.get(3));
    }

    public void testMove() {    
        Property lettersProp = rootDataObject.getInstanceProperty("letters");
        Property numbersProp = rootDataObject.getInstanceProperty("numbers");

        rootDataObject.getSequence().add(lettersProp, "A");
        rootDataObject.getSequence().add(numbersProp, "1");
        rootDataObject.getSequence().add(lettersProp, "B");
        rootDataObject.getSequence().add(numbersProp, "2");
        rootDataObject.getSequence().add(lettersProp, "C");
        rootDataObject.getSequence().add(numbersProp, "3");
        rootDataObject.getSequence().add(lettersProp, "D");
        rootDataObject.getSequence().add(numbersProp, "4");
                      
        assertEquals("A", rootDataObject.getSequence().getValue(0));
        assertEquals("1", rootDataObject.getSequence().getValue(1));        
        assertEquals("B", rootDataObject.getSequence().getValue(2));
        assertEquals("2", rootDataObject.getSequence().getValue(3));
        assertEquals("C", rootDataObject.getSequence().getValue(4));
        assertEquals("3", rootDataObject.getSequence().getValue(5));
        assertEquals("D", rootDataObject.getSequence().getValue(6));
        assertEquals("4", rootDataObject.getSequence().getValue(7));
        
        rootDataObject.getSequence().move(2, 4);
        
        assertEquals("A", rootDataObject.getSequence().getValue(0));
        assertEquals("1", rootDataObject.getSequence().getValue(1));        
        assertEquals("C", rootDataObject.getSequence().getValue(2));
        assertEquals("B", rootDataObject.getSequence().getValue(3));
        assertEquals("2", rootDataObject.getSequence().getValue(4));        
        assertEquals("3", rootDataObject.getSequence().getValue(5));
        assertEquals("D", rootDataObject.getSequence().getValue(6));
        assertEquals("4", rootDataObject.getSequence().getValue(7));        
        
        List lettersListValue = rootDataObject.getList("letters");
        assertEquals("A", lettersListValue.get(0));
        assertEquals("C", lettersListValue.get(1));
        assertEquals("B", lettersListValue.get(2));
        assertEquals("D", lettersListValue.get(3));
        
    }
}
