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
 * dmccann - June 17/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.objectreference.Root;
import org.w3c.dom.Document;

/**
 * Tests XmlType via eclipselink-oxm.xml
 *
 */
public class XmlTypeTestCases extends ExternalizedMetadataTestCases {
    private boolean shouldGenerateSchema = true;
    private MySchemaOutputResolver outputResolver; 
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltype";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmltype/";
    static final int EMP_ID = 101;
    static final String EMP_FIRST = "Joe";
    static final String EMP_LAST = "Oracle";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlTypeTestCases(String name) {
        super(name);
    }
    
    public void setUp() throws Exception {
        super.setUp();
        outputResolver = generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "eclipselink-oxm.xml", 1);
    }
    
    /**
     * Create the control Employee.
     */
    private Employee getControlObject() {
        Employee ctrlEmp = new Employee();
        ctrlEmp.id = EMP_ID;
        ctrlEmp.firstName = EMP_FIRST;
        ctrlEmp.lastName = EMP_LAST;
        return ctrlEmp;
    }
    
    public void testEmployeeSchemaGen() {
        // validate employee schema
        compareSchemas(outputResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(PATH + "employee.xsd"));
    }

    /**
     * Validate instance document(s).
     * 
     * Validates @XmlType override via eclipselink-oxm.xml.  Overrides type 
     * name (my-employee-type) with (employee-type) and propOrder
     * ("id", "firstName", "lastName") with ("id", "lastName", "firstName")
     */
    public void testInstanceDocValidation() {
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
        src = PATH + "employee-invalid.xml";
        result = validateAgainstSchema(src, EMPTY_NAMESPACE, outputResolver);
        assertTrue("Instance doc validation (employee-invalid) succeeded unxepectedly: " + result, result != null);
    }

    public void testUnmarshal() {
        // load instance doc
        InputStream iDocStream = loader.getResourceAsStream(PATH + "employee.xml");
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + PATH + "employee.xml" + "]");
        }

        // setup control Employee
        Employee ctrlEmp = getControlObject();
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Employee empObj = (Employee) unmarshaller.unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", empObj);
            assertTrue("Unmarshal failed:  Employee objects are not equal", ctrlEmp.equals(empObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }
    
    public void testMarshal() {
        // load instance doc
        String src = PATH + "employee.xml";

        // setup control document
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }

        // test marshal
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            Employee ctrlEmp = getControlObject();
            marshaller.marshal(ctrlEmp, testDoc);
            //marshaller.marshal(ctrlEmp, System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }

    public void testEmployeeFactoryClass() {
        // load instance doc
        InputStream iDocStream = loader.getResourceAsStream(PATH + "employee.xml");
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + PATH + "employee.xml" + "]");
        }

        JAXBContext jCtx = null;
        try {
            jCtx = createContext(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "employee-factory-class-oxm.xml");
        } catch (JAXBException e1) {
            fail("JAXBContext creation failed: " + e1.getMessage());
        }

        // setup control Employee
        Employee ctrlEmp = getControlObject();
        try {
            Employee empObj = (Employee) jCtx.createUnmarshaller().unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", empObj);
            assertTrue("Factory method was not called as expected", empObj.fromFactoryMethod);
            assertTrue("Unmarshal failed:  Employee objects are not equal", ctrlEmp.equals(empObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }
}
