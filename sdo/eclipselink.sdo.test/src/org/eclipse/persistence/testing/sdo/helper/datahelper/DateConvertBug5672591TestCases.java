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
package org.eclipse.persistence.testing.sdo.helper.datahelper;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import junit.textui.TestRunner;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.eclipse.persistence.exceptions.SDOException;

public class DateConvertBug5672591TestCases extends SDOTestCase {
    Type rootType;

    public DateConvertBug5672591TestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.sdo.helper.datahelper.DateConvertBug5672591TestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() {
        super.setUp();
        DataObject rootDo = dataFactory.create("commonj.sdo", "Type");
        rootDo.set("name", "root");
        rootDo.set("uri", "theUri");
        addProperty(rootDo, "theProp", SDOConstants.SDO_YEARMONTHDAY, false, false, true);
        addProperty(rootDo, "theManyProp", SDOConstants.SDO_YEARMONTHDAY, false, true, true);
        addProperty(rootDo, "theBooleanProp", SDOConstants.SDO_BOOLEAN, false, false, true);
        addProperty(rootDo, "theBooleanManyProp", SDOConstants.SDO_BOOLEAN, false, true, true);
        rootType = typeHelper.define(rootDo);
    }

    public void testConversion() {
        DataObject testDataObject = dataFactory.create(rootType);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, 1975);
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DATE, 21);
        Date dateValue = cal.getTime();
        testDataObject.setDate(rootType.getProperty("theProp"), dateValue);
        Object getValue = testDataObject.get("theProp");

        //value should be converted to a string with dataHelper
        assertEquals("1975-02-21", getValue);
    }

    public void testExceptionSet() {
        DataObject testDataObject = dataFactory.create(rootType);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, 1975);
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DATE, 21);
        Date dateValue = cal.getTime();
        try {
            //value should be converted to a string with dataHelper but unsupported conversion
            testDataObject.setDate(rootType.getProperty("theBooleanProp"), dateValue);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getCause() instanceof SDOException);
            //assertEquals(SDOException.WRONG_VALUE_FOR_PROPERTY ,((SDOException)e.getCause()).getErrorCode());
            //changed from WRONG_VALUE_FOR_PROPERTY to conversion error...this is because of removal of datahelper.isconversionsupported
            assertEquals(SDOException.CONVERSION_ERROR, ((SDOException)e.getCause()).getErrorCode());
            return;
        }
        fail("An IllegalArgumentException should have occurred");
    }

    public void testConversionAddToList() {
        DataObject testDataObject = dataFactory.create(rootType);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, 1975);
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DATE, 21);
        Date dateValue = cal.getTime();

        testDataObject.getList("theManyProp").add(dateValue);
        Object getValue = testDataObject.getList("theManyProp").get(0);

        //value should be converted to a string with dataHelper
        // assertEquals("1975-02-21", getValue);
        assertEquals(dateValue, getValue);
    }

    public void testConversionSetList() {
        DataObject testDataObject = dataFactory.create(rootType);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, 1975);
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DATE, 21);
        Date dateValue = cal.getTime();

        List theList = new ArrayList();
        theList.add(dateValue);
        testDataObject.setList("theManyProp", theList);
        Object getValue = testDataObject.getList("theManyProp").get(0);

        //value should be converted to a string with dataHelper
        assertEquals("1975-02-21", getValue);
    }

    public void testConversionSetListGenericSet() {
        DataObject testDataObject = dataFactory.create(rootType);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, 1975);
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DATE, 21);
        Date dateValue = cal.getTime();

        List theList = new ArrayList();
        theList.add(dateValue);
        testDataObject.set("theManyProp", theList);
        Object getValue = testDataObject.getList("theManyProp").get(0);

        //no conversion happens with generic set
        assertEquals(dateValue, getValue);
    }

    public void testConversionAddToListException() {
        DataObject testDataObject = dataFactory.create(rootType);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, 1975);
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DATE, 21);
        Date dateValue = cal.getTime();
        List theList = new ArrayList();
        theList.add(dateValue);
        try {
            //value should be converted to a string with dataHelper but unsupported conversion
            testDataObject.setList("theBooleanManyProp", theList);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail("An IllegalArgumentException should have occurred");
    }
}
