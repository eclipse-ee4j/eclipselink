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
package org.eclipse.persistence.testing.sdo.model.sequence;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SequenceJIRA242TestCases extends SDOTestCase {
    private Type customerType;

    public SequenceJIRA242TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.sequence.SequenceJIRA242TestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();
        SDOType typeType = (SDOType) typeHelper.getType(SDOConstants.SDO_URL, SDOConstants.TYPE);
        DataObject customerTypeDO = dataFactory.create(typeType);
        customerTypeDO.set("uri", "someUri");
        customerTypeDO.set("name", "theName");
        customerTypeDO.set("sequenced", true);
        addProperty(customerTypeDO, "firstName", SDOConstants.SDO_STRING, false, false, true);
        addProperty(customerTypeDO, "lastName", SDOConstants.SDO_STRING, false, false, true);
        customerType = typeHelper.define(customerTypeDO);
    }

    public void testJira242Issue() {
        DataObject customer = dataFactory.create(customerType);
        customer.set("lastName", "Doe");
        assertEquals(1, customer.getSequence().size());
        customer.set("firstName", "Jane");
        assertEquals(2, customer.getSequence().size());
        customer.set("lastName", "Jones");
        assertEquals(2, customer.getSequence().size());

        assertEquals("lastName", customer.getSequence().getProperty(0).getName());
        assertEquals("firstName", customer.getSequence().getProperty(1).getName());
        assertEquals("Jones", customer.getSequence().getValue(0));
        assertEquals("Jane", customer.getSequence().getValue(1));
    }
}
