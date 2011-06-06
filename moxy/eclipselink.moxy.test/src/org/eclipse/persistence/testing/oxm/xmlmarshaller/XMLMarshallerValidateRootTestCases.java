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
package org.eclipse.persistence.testing.oxm.xmlmarshaller;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLValidator;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLMarshallerValidateRootTestCases extends OXTestCase {
    private XMLContext xmlContext;
    private XMLValidator xmlValidator;
    private XMLMarshallerCarProject project;

    public XMLMarshallerValidateRootTestCases(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        project = new XMLMarshallerCarProject();
        xmlContext = new XMLContext(project);
        xmlValidator = xmlContext.createValidator();
    }

    public void testDescriptorNotInProject() throws Exception {
        Employee emp = new Employee();
        try {
            xmlValidator.validateRoot(emp);
        } catch (XMLMarshalException ex) {
            assertTrue("", ex.getErrorCode() == XMLMarshalException.DESCRIPTOR_NOT_FOUND_IN_PROJECT);
            return;
        }

        assertTrue("Validation Exception not caught", false);
    }

    public void testDescriptorWithNoSchemaReference() throws Exception {
        ((XMLDescriptor) project.getClassDescriptor(Car.class)).setSchemaReference(null);
        Car car = new Car();
        car.setLicense("123789");
        try {
            xmlValidator.validateRoot(car);
        } catch (XMLMarshalException ex) {
            assertTrue("", ex.getErrorCode() == XMLMarshalException.SCHEMA_REFERENCE_NOT_SET);
            return;
        }
        assertTrue("ValidationException not caught", false);
    }

    public void testValidCar() throws Exception {
        Car car = new Car();
        car.setLicense("123987");
        assertTrue("Valid car reported invalid", xmlValidator.validateRoot(car));
    }

    public void testInvalidCar() throws Exception {
        Car car = new Car();
        car.setLicense("12345678910");
        assertFalse("Invalid car found to be valid", xmlValidator.validateRoot(car));
    }

    public void testErrorHandler() throws Exception {
        ErrorHandler errorHandler = new IgnoreAllErrorHandler();
        xmlValidator.setErrorHandler(errorHandler);

        Car car = new Car();
        car.setLicense("12345678910");
        assertTrue("Errors not ignored", xmlValidator.validateRoot(car));
    }

    public void testNullRootObject() throws Exception {
        try {
            boolean valid = xmlValidator.validateRoot(null);
        } catch (XMLMarshalException validationException) {
            assertTrue("An unexpected XMLMarshalException was caught.", validationException.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
            return;
        }
        assertTrue("An XMLMarshalException should have been caught but wasn't", false);
    }

    public void testNullObject() throws Exception {
        try {
            boolean valid = xmlValidator.validate(null);
        } catch (XMLMarshalException validationException) {
            assertTrue("An unexpected XMLMarshalException was caught.", validationException.getErrorCode() == XMLMarshalException.NULL_ARGUMENT);
            return;
        }
        assertTrue("An XMLMarshalException should have been caught but wasn't", false);
    }

    private static class IgnoreAllErrorHandler implements ErrorHandler {
        public void warning(SAXParseException exception) throws SAXException {
        }

        public void error(SAXParseException exception) throws SAXException {
        }

        public void fatalError(SAXParseException exception) throws SAXException {
        }
    }
}
