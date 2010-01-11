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
/* $Header: SDODataObjectJIRA102NillableDirectTestCases.java 23-nov-2006.14:50:37 dmahar Exp $ */
/*
   DESCRIPTION

   MODIFIED    (MM/DD/YY)
    dmahar      11/23/06 - 
    mfobrien    11/15/06 - Creation
 */

/**
 *  @version $Header: SDODataObjectJIRA102NillableDirectTestCases.java 23-nov-2006.14:50:37 dmahar Exp $
 *  @author  mfobrien
 *  @since   11.1
 */

package org.eclipse.persistence.testing.sdo.model.dataobject;

import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class SDODataObjectJIRA102NillableDirectTestCases extends SDOTestCase {
    private DataObject employeeDataObject;

    public SDODataObjectJIRA102NillableDirectTestCases(String name) {
        super(name);
    }

    public void setUp() {
        super.setUp();
        Type stringType = SDOConstants.SDO_STRING;
        
        // define address subtype of employee /employee/address
        //DataObject addressType = defineType("my.uri", "address");
        //Type addressSDOType = aHelperContext.getTypeHelper().define(addressType); 
        // define city subtype of address /employee/address/city
        //DataObject cityType = defineType("my.uri", "city");
        //Type citySDOType = aHelperContext.getTypeHelper().define(cityType); 
        // add subtype to address
        //addProperty(addressType, "city", stringType);
        
        // define employee supertype
        DataObject empType = defineType("my.uri", "employee");
        

        // add subtypes to employee
        addProperty(empType, "id", stringType);
        addProperty(empType, "last-name", stringType);
        
        // add address property to employee
        //DataObject addressProp = addProperty(empType, "address", addressSDOType);
        Type empSDOType = typeHelper.define(empType);
        //addressProp.set("many", false);//true);

        // create employee object
        employeeDataObject = dataFactory.create(empSDOType);
        
        // create address object
        //DataObject addressDataObject = aHelperContext.getDataFactory().create(addressSDOType);
        //employeeDataObject.set("id", "123");
        employeeDataObject.set("last-name", "Doe");
        // set id nullable
        ((SDOProperty)employeeDataObject.getInstanceProperty("id")).setNullable(true);

        // populate objects
        
        // set composite object
        //ArrayList addresses = new ArrayList();
        //addresses.add(addressDataObject);
        //employeeDataObject.set("address", addressDataObject);
    }

    public void testNullableAddressType() {
        assertFalse(employeeDataObject.isSet("id"));
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectJIRA102NillableDirectTestCases" };
        TestRunner.main(arguments);
    }
    
}
