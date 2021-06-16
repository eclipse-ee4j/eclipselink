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
