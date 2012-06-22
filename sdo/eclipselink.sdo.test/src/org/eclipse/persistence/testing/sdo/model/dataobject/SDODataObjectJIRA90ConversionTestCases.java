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
package org.eclipse.persistence.testing.sdo.model.dataobject;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

public class SDODataObjectJIRA90ConversionTestCases extends SDOTestCase {
    private DataObject companyDataObject;

    public SDODataObjectJIRA90ConversionTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.model.dataobject.SDODataObjectJIRA90ConversionTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();
        Type intType = SDOConstants.SDO_INT;
        Type doubleType = SDOConstants.SDO_DOUBLE;
        Type decimalType = SDOConstants.SDO_DECIMAL;

        DataObject empType = defineType("my.uri", "employee");
        addProperty(empType, "id_int", intType, false, false, true);
        addProperty(empType, "id_double", doubleType, false, false, true);
        addProperty(empType, "id_decimal", decimalType, false, false, true);
        Type empSDOType = typeHelper.define(empType);

        DataObject companyType = defineType("my.uri", "company");
        DataObject empsProp = addProperty(companyType, "emps", empSDOType, true, true, true);        
        Type companySDOType = typeHelper.define(companyType);

        DataObject empDataObject = dataFactory.create(empSDOType);
        companyDataObject = dataFactory.create(companySDOType);
        empDataObject.set("id_int", 123);
        double doubleValue = 123.4444;
        empDataObject.set("id_double", doubleValue);
        empDataObject.set("id_decimal", new BigDecimal("123.4444"));
        ArrayList emps = new ArrayList();
        emps.add(empDataObject);
        companyDataObject.set("emps", emps);

    }

    public void testGetIntType() {
        Object value = companyDataObject.get("emps[id_int=123.4444]");
        assertNull(value);
    }

    public void testGetDoubleType() {
        Object value = companyDataObject.get("emps[id_double=123.4444]");
        assertNotNull(value);
    }

    public void testGetDecimalType() {
        Object value = companyDataObject.get("emps[id_decimal=123.4444]");
        assertNotNull(value);
    }
}
