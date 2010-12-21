/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - August 5/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import junit.textui.TestRunner;

import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests xml-transformation.
 */
public class XmlTransformationTestCases extends ExternalizedMetadataTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/xmltransformation/employee.xml";
    private final static String XSD_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/xmltransformation/employee.xsd";
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/xmltransformation/";
    private static final String METADATA_FILE = PATH + "eclipselink-oxm.xml";
    private static final String METADATA_FILE_METHOD_ATT_TRANSFORMER = PATH + "method-att-xformer.xml";
    private static final String METADATA_FILE_NO_CLASS_OR_METHOD = PATH + "no-class-or-method.xml";
    private static final String METADATA_FILE_CLASS_AND_METHOD = PATH + "both-class-and-method.xml";
    private static final String METADATA_FILE_BAD_METHOD = PATH + "bad-method.xml";
    private static final String METADATA_FILE_BAD_CLASS = PATH + "bad-class.xml";
    private static final String EMP_NAME = "John Smith";
    private static final String START = "9:00AM";
    private static final String END = "5:00PM";
    private Class[] employeeArray;
    private MySchemaOutputResolver employeeResolver;
    private JAXBContext jCtx;

    public XmlTransformationTestCases(String name) throws Exception {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();
        employeeArray = new Class[] { Employee.class };
        jCtx = createContext(employeeArray, CONTEXT_PATH, METADATA_FILE);
    }

    public Employee getControlObject() {
        Employee emp = new Employee();
        emp.setName(EMP_NAME);
        String[] hours = new String[2];
        hours[0] = START;
        hours[1] = END;
        emp.setNormalHours(hours);
        return emp;
    }

    /**
     * Test schema generation and instance document validation.
     * 
     * Positive test.
     */
    public void testSchemaGenAndValidation() {
        employeeResolver = generateSchemaWithFileName(employeeArray, CONTEXT_PATH, METADATA_FILE, 1);
        // validate the schema
        compareSchemas(employeeResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(XSD_RESOURCE));
        String result = validateAgainstSchema(XML_RESOURCE, EMPTY_NAMESPACE, employeeResolver);
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
    }

    /**
     * Test marshal operation.  In this case one field transformer is set via
     * transformer-class, and another is set via method.
     * 
     * Positive test.
     */
    public void testMarshal() {
        // setup control document
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(XML_RESOURCE);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + XML_RESOURCE + "].");
        }
        // setup control Employee
        Employee ctrlEmp = getControlObject();
        
        Marshaller marshaller = jCtx.createMarshaller();
        try {
            //marshaller.marshal(ctrlEmp, System.out);
            marshaller.marshal(ctrlEmp, testDoc);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests unmarshal operation.  Here an attribute transformer class
     * is set on a type.
     * 
     * Positive test.
     */
    public void testUnmarshal() {
        // load instance doc
        InputStream iDocStream = loader.getResourceAsStream(XML_RESOURCE);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + XML_RESOURCE + "]");
        }

        // setup control Employee
        Employee ctrlEmp = getControlObject();

        Unmarshaller unmarshaller = jCtx.createUnmarshaller();
        try {
            Employee empObj = (Employee) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", empObj);
            assertTrue("Unmarshal failed:  Employee objects are not equal", ctrlEmp.equals(empObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }

    /**
     * Tests setting an attribute transformer method name on a type.
     * 
     * Positive test.
     */
    public void testMethodSetOnType() {
        // create a JAXBContext
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = createContext(employeeArray, CONTEXT_PATH, METADATA_FILE_METHOD_ATT_TRANSFORMER);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("JAXBContext creation failed.");
        }

        // load instance doc
        InputStream iDocStream = loader.getResourceAsStream(XML_RESOURCE);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + XML_RESOURCE + "]");
        }

        // setup control Employee
        Employee ctrlEmp = getControlObject();

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        try {
            Employee empObj = (Employee) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", empObj);
            assertTrue("Unmarshal failed:  Employee objects are not equal", ctrlEmp.equals(empObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }
    }

    /**
     * Test exception handling:  in this case both a transformer class and
     * method name have been set.  An exception should be thrown.
     * 
     * Negative test.
     */
    public void testBothClassAndMethod() {
        try {
            JAXBContext jaxbContext = createContext(employeeArray, CONTEXT_PATH, METADATA_FILE_CLASS_AND_METHOD);
        } catch (JAXBException e) {
            return;
        }
        fail("The expected exception was never thrown.");
    }
    
    /**
     * Test exception handling:  in this case no transformer class or
     * method name has been set.  An exception should be thrown.
     * 
     * Negative test.
     */
    public void testNoClassOrMethod() {
        try {
            JAXBContext jaxbContext = createContext(employeeArray, CONTEXT_PATH, METADATA_FILE_NO_CLASS_OR_METHOD);
        } catch (JAXBException e) {
            return;
        }
        fail("The expected exception was never thrown.");
    }
    
    /**
     * Test exception handling:  in this case the method name that has been 
     * set is invalid (wrong number or type of params. An exception should 
     * be thrown.
     * 
     * Negative test.
     */
    public void testInvalidMethod() {
        int exceptionCount = 0;
        // test exception from MappingsGenerator
        try {
            JAXBContext jaxbContext = createContext(employeeArray, CONTEXT_PATH, METADATA_FILE_BAD_METHOD);
        } catch (JAXBException e) {
            exceptionCount++;
        }
        assertTrue("The expected exception was never thrown.", exceptionCount > 0);
        exceptionCount--;
        // test exception from SchemaGenerator
        try {
            HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
            metadataSourceMap.put(CONTEXT_PATH, new StreamSource(loader.getResourceAsStream(METADATA_FILE_BAD_METHOD)));
            Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
            properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
            Map<String, XmlBindings> bindings = JAXBContextFactory.getXmlBindingsFromProperties(properties, loader);
            JavaModelInputImpl jModelInput = new JavaModelInputImpl(employeeArray, new JavaModelImpl(new JaxbClassLoader(loader, employeeArray)));
            Generator generator = new Generator(jModelInput, bindings, loader, "");
            generator.generateSchema();
        } catch (Exception e) {
            exceptionCount++;
        }
        assertTrue("The expected exception was never thrown.", exceptionCount > 0);
    }
    
    /**
     * Test exception handling:  in this case an invalid transformer class
     * has been set.  An exception should be thrown.
     * 
     * Negative test.
     */
    public void testInvalidTransformerClass() {
        int exceptionCount = 0;
        // test exception from MappingsGenerator
        try {
            JAXBContext jaxbContext = createContext(employeeArray, CONTEXT_PATH, METADATA_FILE_BAD_CLASS);
        } catch (JAXBException e) {
            exceptionCount++;
        }
        assertTrue("The expected exception was never thrown.", exceptionCount > 0);
        exceptionCount--;
        // test exception from SchemaGenerator
        try {
            HashMap<String, Source> metadataSourceMap = new HashMap<String, Source>();
            metadataSourceMap.put(CONTEXT_PATH, new StreamSource(loader.getResourceAsStream(METADATA_FILE_BAD_CLASS)));
            Map<String, Map<String, Source>> properties = new HashMap<String, Map<String, Source>>();
            properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, metadataSourceMap);
            Map<String, XmlBindings> bindings = JAXBContextFactory.getXmlBindingsFromProperties(properties, loader);
            JavaModelInputImpl jModelInput = new JavaModelInputImpl(employeeArray, new JavaModelImpl(new JaxbClassLoader(loader, employeeArray)));
            Generator generator = new Generator(jModelInput, bindings, loader, "");
            generator.generateSchema();
        } catch (Exception e) {
            exceptionCount++;
        }
        assertTrue("The expected exception was never thrown.", exceptionCount > 0);
    }
    
    public static void main(String[] args) {
        String[] arguments = { "-c", "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.xmltransformation.XmlTransformationTestCases" };
        TestRunner.main(arguments);
    }
}
