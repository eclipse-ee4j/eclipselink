/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
/* $Header: SDODataObjectJIRA102NillableCompositeObjectTestCases.java 23-nov-2006.14:50:37 dmahar Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    dmahar      11/23/06 -
    mfobrien    11/15/06 - Creation
 */
/**
 *  @version $Header: SDODataObjectJIRA102NillableCompositeObjectTestCases.java 23-nov-2006.14:50:37 dmahar Exp $
 *  @author  mfobrien
 *  @since   11.1
 */
package org.eclipse.persistence.testing.sdo.model.dataobject;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDODataObjectJIRA102NillableCompositeObjectTestCases extends SDOTestCase {
    private DataObject employeeDataObject;

    public SDODataObjectJIRA102NillableCompositeObjectTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        Type stringType = SDOConstants.SDO_STRING;

        // define address subtype of employee /employee/address
        DataObject addressType = defineType("my.uri", "address");
        Type addressSDOType = typeHelper.define(addressType);

        // define city subtype of address /employee/address/city
        //DataObject cityType = defineType("my.uri", "city");
        //Type citySDOType = typeHelper.define(cityType);
        // add subtype to address
        addProperty(addressType, "city", stringType, false, false, true);

        // define employee supertype
        DataObject empType = defineType("my.uri", "employee");

        // add subtypes to employee
        addProperty(empType, "id", stringType, false, false, true);
        addProperty(empType, "last-name", stringType, false, false, true);

        // add address property to employee
        DataObject addressProp = addProperty(empType, "address", addressSDOType, true, false, true);
        Type empSDOType = typeHelper.define(empType);

        // create employee object
        employeeDataObject = dataFactory.create(empSDOType);

        // create address object
        DataObject addressDataObject = dataFactory.create(addressSDOType);
        employeeDataObject.set("id", "123");
        employeeDataObject.set("last-name", "Doe");

        // set address nullable
        ((SDOProperty)employeeDataObject.getInstanceProperty("address")).setNullable(true);

        // populate objects
        // set composite object
        //ArrayList addresses = new ArrayList();
        //addresses.add(addressDataObject);
        //employeeDataObject.set("address", addressDataObject);
    }

    public void testNullableAddressType() {
        DataObject anAddress = (DataObject)employeeDataObject.get("address");
        assertFalse(employeeDataObject.isSet("address"));
        //assertTrue
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectJIRA102NillableCompositeObjectTestCases" };
        TestRunner.main(arguments);
    }
}
