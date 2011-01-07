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

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import junit.textui.TestRunner;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XMLMarshallerValidationModeTestCases extends OXTestCase {
    private static String NO_HEADER_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/Car.xml";
    private static String DTD_VALIDATING_PASSES_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/Car_DTD_passes.xml";
    private static String DTD_VALIDATING_FAILS_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/Car_DTD_fails.xml";
    private static String SCHEMA_VALIDATING_PASSES_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/Car_Valid.xml";
    private static String SCHEMA_VALIDATING_FAILS_RESOURCE = "org/eclipse/persistence/testing/oxm/xmlmarshaller/Car_Invalid.xml";
    private static String CONTROL_LICENSE = "123456";
    private static String CONTROL_LICENSE_INVALID = "123456789";
    private Object controlObject;
    private XMLContext context;
    private XMLUnmarshaller unmarshaller;
    private int originalMode;

    public XMLMarshallerValidationModeTestCases(String name) {
        super(name);
    }

    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.oxm.xmlmarshaller.XMLMarshallerValidationModeTestCases" };
        TestRunner.main(arguments);
    }

    public void setUp() throws Exception {
        context = this.getXMLContext(new XMLMarshallerCarProject());
        unmarshaller = context.createUnmarshaller();
        originalMode = unmarshaller.getValidationMode();

        Car car = new Car();
        car.setLicense(CONTROL_LICENSE);
        controlObject = car;

    }

    public void tearDown() throws Exception {
        unmarshaller.setValidationMode(originalMode);
    }

    //NON VALIDATING TESTS=========================================================================================		

    /**
     * Set the validation mode to NONVALIDATING and try to unmarshal a file which is not valid against the DTD.
     * No Exception should occur
     */
    public void testNonValidatingDTD() {
        unmarshaller.setEntityResolver(new TestEntityResolver());
        unmarshaller.setValidationMode(XMLUnmarshaller.NONVALIDATING);
        InputStream streamToUnmarshal = ClassLoader.getSystemResourceAsStream(DTD_VALIDATING_FAILS_RESOURCE);
        Object unmarshalledObject = unmarshaller.unmarshal(streamToUnmarshal);

        assertEquals(controlObject, unmarshalledObject);

    }

    /**
     * Set the validation mode to NONVALIDATING and try to unmarshal a file which is not valid against the schema.
     * No Exception should occur
     */
    public void testNonValidatingSchema() {
        unmarshaller.setValidationMode(XMLUnmarshaller.NONVALIDATING);
        InputStream streamToUnmarshal = ClassLoader.getSystemResourceAsStream(SCHEMA_VALIDATING_FAILS_RESOURCE);
        Object unmarshalledObject = unmarshaller.unmarshal(streamToUnmarshal);

        Car car = new Car();
        car.setLicense(CONTROL_LICENSE_INVALID);

        assertEquals(car, unmarshalledObject);

    }

    /**
     * Set the validation mode to NONVALIDATING and try to unmarshal a file which does not have a DTD or schema set
     * No Exception should occur
     */
    public void testNonValidatingNoHeader() {
        unmarshaller.setValidationMode(XMLUnmarshaller.NONVALIDATING);
        InputStream streamToUnmarshal = ClassLoader.getSystemResourceAsStream(NO_HEADER_RESOURCE);
        Object unmarshalledObject = unmarshaller.unmarshal(streamToUnmarshal);

        assertEquals(controlObject, unmarshalledObject);

    }

    //DTD TESTS =========================================================================================

    /**
     * Set the validation mode to DTDVALIDATION and try to unmarshal a file with no DTD specified
     * An XMLMarshalException should occur
     */
    public void testDTDValidatingDTDNotSet() {
        unmarshaller.setEntityResolver(new TestEntityResolver());
        unmarshaller.setValidationMode(XMLUnmarshaller.DTD_VALIDATION);
        ErrorHdlr err = new ErrorHdlr();
        unmarshaller.setErrorHandler(err);
        InputStream streamToUnmarshal = ClassLoader.getSystemResourceAsStream(NO_HEADER_RESOURCE);
        try {
            unmarshaller.unmarshal(streamToUnmarshal);
        } catch (XMLMarshalException exception) {
			StringWriter sw = new StringWriter();
			exception.printStackTrace(new PrintWriter(sw));
			assertTrue("An unexpected XMLMarshalException was caught\n" + sw.toString(), exception.getErrorCode() == XMLMarshalException.UNMARSHAL_EXCEPTION);
			return;
        }

        assertTrue("An exception should have been caught but wasn't.", false);
    }

    /**
     * Set the validation mode to DTDVALIDATION and try to unmarshal a file which is valid against the DTD
     * No Exception should occur
     */
    public void testDTDValidatingPasses() {
        unmarshaller.setEntityResolver(new TestEntityResolver());
        unmarshaller.setValidationMode(XMLUnmarshaller.DTD_VALIDATION);
        ErrorHdlr err = new ErrorHdlr();
        unmarshaller.setErrorHandler(err);
        InputStream streamToUnmarshal = ClassLoader.getSystemResourceAsStream(DTD_VALIDATING_PASSES_RESOURCE);
        Object unmarshalledObject;
        try {
            unmarshalledObject = unmarshaller.unmarshal(streamToUnmarshal);
        } catch (XMLMarshalException exception) {
			StringWriter sw = new StringWriter();
			exception.printStackTrace(new PrintWriter(sw));
			assertTrue("An XMLMarshalException was thrown unexpectedly\n" + sw.toString(), false);
			return;
        }
        assertEquals(controlObject, unmarshalledObject);
    }

    /**
     * Set the validation mode to DTDVALIDATION and try to unmarshal a file which is NOT valid against the DTD
     * An XMLMarshalException should occur
     */
    public void testDTDValidatingFails() {
        try {
            unmarshaller.setEntityResolver(new TestEntityResolver());
            unmarshaller.setValidationMode(XMLUnmarshaller.DTD_VALIDATION);
            ErrorHdlr err = new ErrorHdlr();
            unmarshaller.setErrorHandler(err);
            InputStream streamToUnmarshal = ClassLoader.getSystemResourceAsStream(DTD_VALIDATING_FAILS_RESOURCE);
            Object unmarshalledObject = unmarshaller.unmarshal(streamToUnmarshal);
            unmarshaller.setValidationMode(originalMode);
            assertEquals(controlObject, unmarshalledObject);
        } catch (XMLMarshalException exception) {
			StringWriter sw = new StringWriter();
			exception.printStackTrace(new PrintWriter(sw));
			assertTrue("An unexpected XMLMarshalException was caught\n" + sw.toString(), exception.getErrorCode() == XMLMarshalException.UNMARSHAL_EXCEPTION);
			return;
        }
        assertTrue(false);
    }

    //SCHEMA VALIDATING TEST=========================================================================================	

    /**
     * Set the validation mode to SCHEMA_VALIDATION and try to unmarshal a file which does not have a schema specified
     * No exception should occur
     */
    public void testSchemaValidatingSchemaNotSet() {
        unmarshaller.setValidationMode(XMLUnmarshaller.SCHEMA_VALIDATION);
        ErrorHdlr err = new ErrorHdlr();
        unmarshaller.setErrorHandler(err);
        InputStream streamToUnmarshal = ClassLoader.getSystemResourceAsStream(NO_HEADER_RESOURCE);
        Object unmarshalledObject;
        try {
            unmarshalledObject = unmarshaller.unmarshal(streamToUnmarshal);
        } catch (XMLMarshalException exception) {
			StringWriter sw = new StringWriter();
			exception.printStackTrace(new PrintWriter(sw));
			assertTrue("An XMLMarshalException was thrown unexpectedly\n " + sw.toString(), false);
			return;
        }
        assertEquals(controlObject, unmarshalledObject);
    }

    /**
     * Set the validation mode to SCHEMA_VALIDATION and try to unmarshal a file which is NOT valid against its schema
     * An XMLMarshalException should occur
     */
    public void testSchemaURLValidatingFails() {
        unmarshaller.setValidationMode(XMLUnmarshaller.SCHEMA_VALIDATION);
        ErrorHdlr err = new ErrorHdlr();
        unmarshaller.setErrorHandler(err);
        InputStream streamToUnmarshal = ClassLoader.getSystemResourceAsStream(SCHEMA_VALIDATING_FAILS_RESOURCE);
        try {
            unmarshaller.unmarshal(streamToUnmarshal);
        } catch (XMLMarshalException exception) {
			StringWriter sw = new StringWriter();
			exception.printStackTrace(new PrintWriter(sw));
			assertTrue("An unexpected XMLMarshalException was caught\n" + sw.toString(), exception.getErrorCode() == XMLMarshalException.UNMARSHAL_EXCEPTION);
			return;
        }
        assertTrue("An XMLMarshalException should have been caught but wasn't.", false);
    }

    /**
     * Set the validation mode to SCHEMA_VALIDATION and try to unmarshal a file which is valid against its schema
     * No exception should occur
     */
    public void testSchemaURLValidatingPasses() {
        unmarshaller.setValidationMode(XMLUnmarshaller.SCHEMA_VALIDATION);
        ErrorHdlr err = new ErrorHdlr();
        unmarshaller.setErrorHandler(err);
        InputStream streamToUnmarshal = ClassLoader.getSystemResourceAsStream(SCHEMA_VALIDATING_PASSES_RESOURCE);
        Object unmarshalledObject;
        try {
            unmarshalledObject = unmarshaller.unmarshal(streamToUnmarshal);
        } catch (XMLMarshalException exception) {
			StringWriter sw = new StringWriter();
			exception.printStackTrace(new PrintWriter(sw));
			assertTrue("An XMLMarshalException was thrown unexpectedly\n" + sw.toString(), false);
			return;
        }
        assertEquals(controlObject, unmarshalledObject);
    }

    //=========================================================================================
    public class TestEntityResolver implements org.xml.sax.EntityResolver {
        private final String DTD_NAME = "org/eclipse/persistence/testing/oxm/xmlmarshaller/Car.dtd";

        public TestEntityResolver() {
        }

        public InputSource resolveEntity(String publicId, String systemId) {
            InputStream localDtdStream = ClassLoader.getSystemResourceAsStream(DTD_NAME);
            if (localDtdStream != null) {
                return new InputSource(localDtdStream);
            }
            return null;
        }
    }
    
    /**
     * Error handler implementation to capture DTD/Schema validation
     * errors/warnings.
     */
    class ErrorHdlr implements ErrorHandler {
    	private boolean errorHasOccurred;
    	private boolean fatalErrorHasOccurred;
    	private boolean warningHasOccurred;
    	ErrorHdlr() {
    		errorHasOccurred = false;
    		fatalErrorHasOccurred = false;
    		warningHasOccurred = false;
    	}
    	public void error(SAXParseException arg0) throws SAXException {
    		errorHasOccurred = true;
    		throw arg0;
    	}
    	public void fatalError(SAXParseException arg0) throws SAXException {
    		fatalErrorHasOccurred = true;
    		throw arg0;
    	}
    	public void warning(SAXParseException arg0) throws SAXException {
    		warningHasOccurred = true;
    		throw arg0;
    	}
    	boolean validationErrorsOccurred() {
    		return errorHasOccurred || fatalErrorHasOccurred || warningHasOccurred;
    	}
    }
}
