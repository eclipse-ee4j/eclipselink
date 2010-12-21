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
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

import java.io.File;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationException;
import javax.xml.bind.Validator;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.Car;
import junit.framework.TestCase;

public class ValidatorTestCases extends TestCase {
    private final static String CONTROL_XML_FILE_NAME = "org/eclipse/persistence/testing/oxm/jaxb/Employee_WithAddresses.xml";
    private final static String CONTROL_XML_ADDRESS_FAIL = "org/eclipse/persistence/testing/oxm/jaxb/Employee_WithAddresses_Fail.xml";
    private final static String CONTROL_XML_INHERITANCE_FILE_NAME = "org/eclipse/persistence/testing/oxm/jaxb/Employee_WithPOBoxAddress.xml";
    private final static String CONTROL_JOB_FILE_NAME = "org/eclipse/persistence/testing/oxm/jaxb/Job_Empty.xml";
    private JAXBContext jaxbContext;
    private Validator validator;
    private Unmarshaller unmarshaller;
    private String contextPath;

    public ValidatorTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        contextPath = System.getProperty("jaxb.test.contextpath", JAXBSAXTestSuite.CONTEXT_PATH);

        jaxbContext = JAXBContext.newInstance(contextPath);
        validator = jaxbContext.createValidator();
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    public void testValidateRoot() throws Exception {
        File file = new File(ClassLoader.getSystemResource(CONTROL_XML_FILE_NAME).getFile());
        Object testObject = unmarshaller.unmarshal(file);

        assertTrue("Object did not validate.", validator.validateRoot(testObject));
    }

    public void testValidateElement() throws Exception {
        boolean caughtException = false;
        File file = new File(ClassLoader.getSystemResource(CONTROL_XML_FILE_NAME).getFile());
        Employee employee = (Employee)unmarshaller.unmarshal(file);

        try {
            validator.validate(employee);
        } catch (ValidationException xmlpe) {
            caughtException = true;
        }
        assertFalse("Employee object did not validate.", caughtException);
    }

    public void testValidateComplexType() throws Exception {
        boolean caughtException = false;
        File file = new File(ClassLoader.getSystemResource(CONTROL_XML_FILE_NAME).getFile());
        Employee employee = (Employee)unmarshaller.unmarshal(file);

        try {
            validator.validate(employee.getHomeAddress());
        } catch (ValidationException xmlpe) {
            caughtException = true;
        }
        assertFalse("Address object did not validate.", caughtException);
    }

    public void testValidateInheritanceComplexType() throws Exception {
        boolean caughtException = false;
        File file = new File(ClassLoader.getSystemResource(CONTROL_XML_INHERITANCE_FILE_NAME).getFile());
        Employee employee = (Employee)unmarshaller.unmarshal(file);

        try {
            validator.validate(employee.getHomeAddress());
        } catch (org.eclipse.persistence.platform.xml.XMLPlatformException xmlpe) {
            caughtException = true;
        }
        assertFalse("Address object did not validate.", caughtException);
    }

    public void testValidateSimpleType() throws Exception {
        boolean caughtException = false;
        File file = new File(ClassLoader.getSystemResource(CONTROL_XML_FILE_NAME).getFile());
        Employee employee = (Employee)unmarshaller.unmarshal(file);

        try {
            validator.validate(employee.getPhone());
        } catch (ValidationException xmlpe) {
            caughtException = true;
        }
        assertFalse("Phone object did not validate.", caughtException);
    }

    public void testValidateSimpleTypeID() throws Exception {
        boolean caughtException = false;
        File file = new File(ClassLoader.getSystemResource(CONTROL_XML_FILE_NAME).getFile());
        Employee employee = (Employee)unmarshaller.unmarshal(file);

        try {
            validator.validate(employee.getBadge());
        } catch (ValidationException xmlpe) {
            caughtException = true;
        }
        assertFalse("Badge object did not validate.", caughtException);
    }

    public void testValidateElementException() throws Exception {
        boolean caughtException = false;

        try {
            Employee employee = new Employee();
            validator.validate(employee);
        } catch (ValidationException xmlpe) {
            caughtException = true;
        } catch (Exception ex) {
        }
        assertTrue("JAXBValidator did not throw ValidationException as expected.", caughtException);
    }

    public void testValidateAgainstWrongSchema() throws Exception {
        Job2 job = new Job2();

        try {
            validator.validate(job);
        } catch (ValidationException validationException) {
            assertTrue(true);
            return;
        } catch (Exception ex) {
            fail("An incorrect Exception was thrown");
            return;
        }
        fail("JAXBValidator did not throw ValidationException as expected.");
    }

    public void testValidateNestedComplexType() throws Exception {
        Job job = new Job();

        try {
            validator.validate(job);
        } catch (Exception exception) {
            fail("JAXBValidator threw an unexpected exception." + exception.getMessage());
            return;
        }
    }

    public void testValidateComplexTypeException() throws Exception {
        boolean caughtException = false;

        try {
            Address address = new Address();
            address.setStreet("613 Cedar Lane");
            address.setCity("Bangor");
            address.setState("Maine");
            validator.validate(address);
        } catch (ValidationException xmlpe) {
            caughtException = true;
        } catch (Exception ex) {
        }
        assertTrue("JAXBValidator did not throw ValidationException as expected.", caughtException);
    }

    public void testValidateSimpleTypeException() throws Exception {
        boolean caughtException = false;

        try {
            Phone phone = new Phone();
            phone.setNumber("12345678901");
            validator.validate(phone);
        } catch (ValidationException xmlpe) {
            caughtException = true;
        } catch (Exception ex) {
        }
        assertTrue("JAXBValidator did not throw ValidationException as expected.", caughtException);
    }

    public void testValidateSimpleTypeIDException() throws Exception {
        boolean caughtException = false;

        try {
            Badge badge = new Badge();
            badge.setNumber("11");
            validator.validate(badge);
        } catch (ValidationException xmlpe) {
            caughtException = true;
        } catch (Exception ex) {
        }
        assertTrue("JAXBValidator did not throw ValidationException as expected.", caughtException);
    }

    public void testValidationEventHandler() throws Exception {
        boolean caughtException = false;
        File file = new File(ClassLoader.getSystemResource(CONTROL_XML_ADDRESS_FAIL).getFile());
        Employee employee = (Employee)unmarshaller.unmarshal(file);

        try {
            validator.setEventHandler(new ValidationEventHandlerImpl());
            validator.validate(employee.getHomeAddress());
        } catch (ValidationException xmlpe) {
            caughtException = true;
        } catch (Exception ex) {
        }
        assertFalse("The event handler did not consume the exceptions as expected.", caughtException);
    }

    public void testValidateException() {
        boolean caughtException = false;
        try {
            validator.validate(null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBValidator did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testValidateRootNull() {
        boolean caughtException = false;
        try {
            validator.validateRoot(null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBValidator did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testValidateRootException() {
        boolean caughtException = false;
        try {
            Car car = new Car();
            validator.validateRoot(car);
        } catch (ValidationException e) {
            assertTrue(true);
            return;
        } catch (Exception e) {
            fail("JAXBValidator did not throw ValidationException as expected.");
            return;
        }
        assertTrue("JAXBValidator did not any exceptions, expected a ValidationException.", caughtException);
    }

    public void testSetNullPropertyException() {
        boolean caughtException = false;
        try {
            validator.setProperty(null, null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBValidator did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testGetNullPropertyException() {
        boolean caughtException = false;
        try {
            validator.getProperty(null);
        } catch (IllegalArgumentException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBValidator did not throw IllegalArgumentException as expected.", caughtException);
    }

    public void testSetInvalidPropertyException() {
        boolean caughtException = false;
        try {
            validator.setProperty("thisIsAnInvalidProperty", "thisIsAnInvalidValue");
        } catch (PropertyException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBValidator did not throw PropertyException as expected.", caughtException);
    }

    public void testGetInvalidPropertyException() {
        boolean caughtException = false;
        try {
            validator.getProperty("thisIsAnInvalidProperty");
        } catch (PropertyException e) {
            caughtException = true;
        } catch (Exception e) {
        }
        assertTrue("JAXBValidator did not throw PropertyException as expected.", caughtException);
    }

    //=============================== Inner class for eventhandler test =================//
    class ValidationEventHandlerImpl implements javax.xml.bind.ValidationEventHandler {
        public boolean handleEvent(javax.xml.bind.ValidationEvent param1) {
            return true;
        }
    }
}
