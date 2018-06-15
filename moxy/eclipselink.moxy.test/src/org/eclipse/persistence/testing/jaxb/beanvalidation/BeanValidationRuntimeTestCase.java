/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6 - initial implementation
package org.eclipse.persistence.testing.jaxb.beanvalidation;

import org.eclipse.persistence.exceptions.BeanValidationException;
import org.eclipse.persistence.jaxb.BeanValidationMode;
import org.eclipse.persistence.jaxb.ConstraintViolationWrapper;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.beanvalidation.dom.Department;
import org.eclipse.persistence.testing.jaxb.beanvalidation.dom.Drivers;
import org.eclipse.persistence.testing.jaxb.beanvalidation.dom.DrivingLicense;
import org.eclipse.persistence.testing.jaxb.beanvalidation.dom.Employee;
import org.junit.After;
import org.junit.Before;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.File;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *  Test suite for tests that can be run during runtime, i.e.
 *  all that don't relate to XJC or SchemaGen.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
public class BeanValidationRuntimeTestCase extends junit.framework.TestCase {

    private static final String RESOURCES_PATH_RELATIVE = "org/eclipse/persistence/testing/jaxb/beanvalidation/rt/";
    private static final String JAVAX_NOT_NULL_MESSAGE = "{javax.validation.constraints.NotNull.message}";
    private static final String JAVAX_MIN_MESSAGE = "{javax.validation.constraints.Min.message}";
    private static final String JAVAX_SIZE_MESSAGE = "{javax.validation.constraints.Size.message}";
    private static final String JAVAX_PATTERN_MESSAGE = "{javax.validation.constraints.Pattern.message}";
    private static final String JAVAX_FUTURE_MESSAGE = "{javax.validation.constraints.Future.message}";
    private static final String JAVAX_DIGITS_MESSAGE = "{javax.validation.constraints.Digits.message}";
    private static final Class[] EMPLOYEE = new Class[]{Employee.class};
    private static final boolean DEBUG = false;

    private static File FILE_VALID;
    private static File FILE_INVALID;
    private static File FILE_JSON_VALID;
    private static File FILE_JSON_INVALID;

    private boolean toggle = true; // Value is sensitive to the order of methods in testBeanValidation() method.
    private ValidatorFactory preferredValidatorFactory;
    private JAXBMarshaller marshallerValidOn;
    private JAXBMarshaller marshallerValidOff;
    private JAXBUnmarshaller unmarshallerValidOn;
    private JAXBUnmarshaller unmarshallerValidOff;
    private Employee employeeValid = new Employee()
            .withId(0xCAFEBABE)
            .withAge(15)
            .withPersonalName("Richard")
            .withPhoneNumber("(420)287-4422")
            .withDepartment(Department.JavaEE)
            .withDrivingLicense(new DrivingLicense(3326, new GregorianCalendar(2029, 12, 31).getTime()));
    private Employee employeeInvalid = new Employee()
            .withAge(15)
            .withPersonalName("Wo")
            .withPhoneNumber("287-4422")
            .withDrivingLicense(new DrivingLicense(1234567, new GregorianCalendar(2010, 5, 20).getTime()));
    // Order is good just for debug. The CVs themselves aren't ordered.
    private AbstractSequentialList<String> violationMessages = new LinkedList<String>(){
        {
            add(JAVAX_NOT_NULL_MESSAGE);   // id
            add(JAVAX_MIN_MESSAGE);        // age
            add(JAVAX_SIZE_MESSAGE);       // personalName
            add(JAVAX_PATTERN_MESSAGE);    // phoneNumber
            add(JAVAX_NOT_NULL_MESSAGE);   // department
            add(JAVAX_FUTURE_MESSAGE);     // drivingLicense.validThrough
            add(JAVAX_DIGITS_MESSAGE);     // drivingLicense.id
        }};
    private AbstractSequentialList<String> violationMessagesWithoutGroup = new LinkedList<String>(){
        {
            add(JAVAX_NOT_NULL_MESSAGE);   // id
            add(JAVAX_SIZE_MESSAGE);       // personalName
            add(JAVAX_PATTERN_MESSAGE);    // phoneNumber
            add(JAVAX_NOT_NULL_MESSAGE);   // department
        }};

    /**
     * Tests the Bean Validation for MOXy Runtime, with Target groups.
     * Tested features:
     *  - setting properties (mode, preferred validation factory) through JAXBContext,
     *      marshaller and unmarshaller, and overriding them,
     *  - validation on valid objects before marshalling and after unmarshalling,
     *  - validation on invalid objects before marshalling and after unmarshalling,
     *  - validation with Target groups,
     *  - retrieval of correct error messages when constraint violations happen.
     *  Everything is tested with both XML and JSON marshalling and unmarshalling.
     */
    public void testBeanValidation() throws Exception{
        validEmployee(FILE_VALID);
        invalidEmployee(FILE_INVALID);
        switchToJson();
        validEmployee(FILE_JSON_VALID);
        invalidEmployee(FILE_JSON_INVALID);

        clearResources();
    }

    private void validEmployee(File file) throws Exception {

        toggleDriversGroupOnOff();

        marshallerValidOn.marshal(employeeValid, file);
        assertTrue(marshallerValidOn.getConstraintViolations().isEmpty());

        Employee employeeUnm = (Employee) unmarshallerValidOn.unmarshal(file);
        assertTrue(unmarshallerValidOn.getConstraintViolations().isEmpty());

        assertEquals(employeeValid, employeeUnm);
    }

    private void invalidEmployee(File fileInvalid) throws Exception {

        JAXBException exception = null;
        toggleDriversGroupOnOff();

        /* Marshal w/ validation - doesn't pass (we want to check that). */
        try {
            marshallerValidOn.marshal(employeeInvalid, fileInvalid);
        } catch (JAXBException e) {
            exception = e;
        }
        assertNotNull(exception);
        assertEquals(String.valueOf(BeanValidationException.CONSTRAINT_VIOLATION), exception.getErrorCode());
        if (DEBUG) System.out.println(exception.getMessage());
        checkValidationMessages(marshallerValidOn.getConstraintViolations(), violationMessages);

        /* Marshal w/o validation - creates file for the next part of the test. */
        marshallerValidOff.marshal(employeeInvalid, fileInvalid);
        Set<ConstraintViolationWrapper<Object>> marshalCV = marshallerValidOff.getConstraintViolations();
        assertTrue(marshalCV.isEmpty());

        /* Unmarshal w/ validation - doesn't pass (we want to check that). */
        exception = null;
        try { unmarshallerValidOn.unmarshal(fileInvalid); }
        catch (JAXBException e) { exception = e; }
        assertNotNull(exception);
        assertEquals(String.valueOf(BeanValidationException.CONSTRAINT_VIOLATION), exception.getErrorCode());
        if (DEBUG) System.out.println(exception.getMessage());
        checkValidationMessages(unmarshallerValidOn.getConstraintViolations(), violationMessages);

        /* Unmarshal w/ validation AND no groups - doesn't pass (we want to check that). */
        toggleDriversGroupOnOff();
        exception = null;

        try { unmarshallerValidOn.unmarshal(fileInvalid); }
        catch (JAXBException e) { exception = e; }
        assertNotNull(exception);
        assertEquals(String.valueOf(BeanValidationException.CONSTRAINT_VIOLATION), exception.getErrorCode());
        if (DEBUG) System.out.println(exception.getMessage());
        checkValidationMessages(unmarshallerValidOn.getConstraintViolations(), violationMessagesWithoutGroup);
        toggleDriversGroupOnOff();

        /* Unmarshal w/o validation - testing that invalid objects are correctly unmarshalled when validation is NONE. */
        Employee employeeUnm = (Employee) unmarshallerValidOff.unmarshal(fileInvalid);
        assertTrue(unmarshallerValidOff.getConstraintViolations().isEmpty());

        /* Final check that the validation feature did not affect original behavior of JAXB. */
        assertEquals(employeeInvalid, employeeUnm);
    }

    private void checkValidationMessages(Set<ConstraintViolationWrapper<Object>> constraintViolations,
                                         List<String> expectedMessages) {
        List<String> violationMessages = new ArrayList<>();
        for (final ConstraintViolationWrapper cv : constraintViolations) {
            violationMessages.add(cv.getMessageTemplate());
        }

        assertSame(expectedMessages.size(), violationMessages.size());
        assertTrue(violationMessages.containsAll(expectedMessages));
    }

    private void toggleDriversGroupOnOff() throws PropertyException {
        if (toggle ^= true) {
            marshallerValidOn.setProperty(MarshallerProperties.BEAN_VALIDATION_GROUPS, new Class[]{Default.class,
                    Drivers.class});
            unmarshallerValidOn.setProperty(MarshallerProperties.BEAN_VALIDATION_GROUPS, new Class[] { Default.class,
                   Drivers.class });
        } else {
            marshallerValidOn.setProperty(MarshallerProperties.BEAN_VALIDATION_GROUPS, new Class[0]);
            unmarshallerValidOn.setProperty(MarshallerProperties.BEAN_VALIDATION_GROUPS, new Class[0]);
        }
    }

    private void switchToJson() throws PropertyException {
        marshallerValidOn.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
        marshallerValidOn.setProperty(JAXBContextProperties.JSON_INCLUDE_ROOT, true);
        unmarshallerValidOn.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
        unmarshallerValidOn.setProperty(JAXBContextProperties.JSON_INCLUDE_ROOT, true);
        marshallerValidOff.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
        marshallerValidOff.setProperty(JAXBContextProperties.JSON_INCLUDE_ROOT, true);
        unmarshallerValidOff.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
        unmarshallerValidOff.setProperty(JAXBContextProperties.JSON_INCLUDE_ROOT, true);
    }

    private void clearResources() {
        assertTrue(FILE_VALID.delete());
        assertTrue(FILE_INVALID.delete());
        assertTrue(FILE_JSON_VALID.delete());
        assertTrue(FILE_JSON_INVALID.delete());
    }

    @Before
    public void setUp() throws Exception {
        preferredValidatorFactory = Validation.buildDefaultValidatorFactory();

        JAXBContext ctx = JAXBContextFactory.createContext(EMPLOYEE, null);
        marshallerValidOn = (JAXBMarshaller) ctx.createMarshaller();
        marshallerValidOn.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshallerValidOff = (JAXBMarshaller) ctx.createMarshaller();
        /* tests setting the property through marshaller */
        marshallerValidOff.setProperty(MarshallerProperties.BEAN_VALIDATION_MODE, BeanValidationMode.NONE);
        marshallerValidOff.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        JAXBContext ctxValidationOff = JAXBContextFactory.createContext(EMPLOYEE,
                new HashMap<String, Object>(){{
                    put(JAXBContextProperties.BEAN_VALIDATION_MODE, BeanValidationMode.NONE);
                    put(JAXBContextProperties.BEAN_VALIDATION_FACTORY, preferredValidatorFactory);}});
        unmarshallerValidOn = (JAXBUnmarshaller) ctxValidationOff.createUnmarshaller();
        /* tests setting the property through unmarshaller */
        unmarshallerValidOn.setProperty(UnmarshallerProperties.BEAN_VALIDATION_MODE, BeanValidationMode.CALLBACK);
        unmarshallerValidOff = (JAXBUnmarshaller) ctxValidationOff.createUnmarshaller();

        String RESOURCES_PATH = Thread.currentThread().getContextClassLoader().getResource(RESOURCES_PATH_RELATIVE).getPath();
        FILE_VALID = new File(RESOURCES_PATH + "employee.xml");
        FILE_INVALID = new File(RESOURCES_PATH + "employeeInvalid.xml");
        FILE_JSON_VALID = new File(RESOURCES_PATH + "employee.json");
        FILE_JSON_INVALID = new File(RESOURCES_PATH + "employeeInvalid.json");
    }

    @After
    public void tearDown() throws Exception {
        marshallerValidOn = marshallerValidOff = null;
        unmarshallerValidOn = unmarshallerValidOff = null;
        employeeValid = employeeInvalid = null;
        violationMessages = null;
        preferredValidatorFactory = null;
    }

}
