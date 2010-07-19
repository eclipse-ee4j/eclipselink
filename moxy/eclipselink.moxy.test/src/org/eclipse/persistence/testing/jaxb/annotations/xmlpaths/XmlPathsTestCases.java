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
 * Oracle = 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpaths;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlChoiceObjectMappings via eclipselink-oxm.xml
 * 
 */
public class XmlPathsTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.annotations.xmlpaths";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/annotations/xmlpaths/";
    private static final String INT_VAL = "66";
    private static final String FLT_VAL = "66.66";
    
    private MySchemaOutputResolver resolver;

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public XmlPathsTestCases(String name) {
        super(name);
    }
    
    /**
     * This method's primary purpose id to generate schema(s). Validation of
     * generated schemas will occur in the testXXXGen method(s) below. Note that
     * the JAXBContext is created from this call and is required for
     * marshal/unmarshal, etc. tests.
     * 
     */
    public void setUp() throws Exception {
        super.setUp();
        resolver = generateSchemaWithFileName(new Class[] { }, CONTEXT_PATH, PATH + "employee-oxm.xml", 1);
    }


    /**
     * Return the control Employee.
     * 
     * @return
     */
    public Employee getControlObject() {
        Employee emp = new Employee();
        emp.thing = new Integer(INT_VAL);
        return emp;
    }
    
    public void testEmployeeSchemaGen() {
        // validate the schema
        compareSchemas(resolver.schemaFiles.get(EMPTY_NAMESPACE), new File(PATH + "employee.xsd"));
    }

    public void testInstanceDocValidation() {
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, resolver);
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
    }
    
    /**
     * Tests XmlChoiceMapping configuration via eclipselink-oxm.xml. 
     * Here an unmarshal operation is performed. Utilizes xml-attribute and 
     * xml-element.
     * 
     * Positive test.
     */
    public void testChoiceMappingUnmarshal() {
        // load instance doc
        InputStream iDocStream = loader.getResourceAsStream(PATH + "employee.xml");
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + PATH + "employee.xml" + "]");
        }

        // setup control Employee
        Employee ctrlEmp = getControlObject();
        // writeOnlyThing should not be read in
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

    /**
     * Tests XmlChoiceMapping configuration via eclipselink-oxm.xml. Here a
     * marshal operation is performed. Utilizes xml-attribute and xml-element
     * 
     * Positive test.
     */
    public void testChoiceMappingMarshal() {
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
}