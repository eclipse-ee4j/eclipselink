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
 * dmccann - March 24/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.directcollection;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

/**
 * Tests XmlDirectCollectionMapping via eclipselink-oxm.xml
 *
 */
public class DirectCollectionMappingTestCases extends ExternalizedMetadataTestCases {
    private static final String CONTEXT_PATH = "org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.directcollection";
    private static final String PATH = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/directcollection/";
    
    private MySchemaOutputResolver employeeResolver;

    private static final int EMPID = 101;
    private static final String PRJ_ID1 = "01";
    private static final String PRJ_ID2 = "10";
    private static final String PRJ_ID3 = "11";
    
    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     */
    public DirectCollectionMappingTestCases(String name) {
        super(name);
    }

    /**
     * Create the control Employee.
     */
    private Employee getControlObject(boolean nullProjectIds) {
        List<String> prjIds = new ArrayList<String>();
        if (nullProjectIds) {
            prjIds.add(null);
        } else {
            prjIds.add(PRJ_ID1);
            prjIds.add(PRJ_ID2);
        }
        prjIds.add(PRJ_ID3);
        
        Employee ctrlEmp = new Employee();
        ctrlEmp.id = EMPID;
        ctrlEmp.projectIds = prjIds;
        return ctrlEmp;
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
        employeeResolver = generateSchemaWithFileName(new Class[] { Employee.class }, CONTEXT_PATH, PATH + "eclipselink-oxm.xml", 1);
    }

    /**
     * Tests schema generation for XmlDirectMapping via eclipselink-oxm.xml.
     * Utilizes xml-attribute and xml-element. xml-value is tested separately
     * below.
     * 
     * Positive test.
     */
    public void testDirectCollecitonSchemaGen() {
        // validate schemas
        compareSchemas(employeeResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(PATH + "employees.xsd"));
    }

    /**
     * Tests XmlDirectCollectionMapping configuration via eclipselink-oxm.xml.
     * Here an unmarshal operation is performed.  
     * 
     * Positive test.
     */
    public void testDirectCollectionUnmarshal() {
        // load instance doc
        String src = PATH + "employee.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }
        try {
            Employee empObj = (Employee) jaxbContext.createUnmarshaller().unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", empObj);
            assertTrue("Accessor method was not called as expected", empObj.wasSetCalled);
            assertTrue("Unmarshal failed:  Employee objects are not equal", getControlObject(false).equals(empObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }
    
    /**
     * Tests XmlDirectCollectionMapping configuration via eclipselink-oxm.xml.
     * Here a marshal operation is performed.  
     * 
     * Positive test.
     */
    public void testDirectCollectionMarshal() {
        // setup control document
        String src = PATH + "employee.xml";
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }
        try {
            Employee ctrlEmp = getControlObject(false);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(ctrlEmp, testDoc);
            //marshaller.marshal(ctrlEmp, System.out);
            assertTrue("Accessor method was not called as expected", ctrlEmp.wasGetCalled);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }
    
    /**
     * Tests XmlDirectCollectionMapping configuration via eclipselink-oxm.xml.
     * Here an unmarshal operation is performed.  
     * 
     * Positive test.
     */
    public void testDirectCollectionNullPolicyUnmarshal() {
        // load instance doc
        String src = PATH + "employee-null-projectids.xml";
        InputStream iDocStream = loader.getResourceAsStream(src);
        if (iDocStream == null) {
            fail("Couldn't load instance doc [" + src + "]");
        }
        // tewak control object (unmarshal null will result in "" being set in the object)
        Employee ctrlEmp = getControlObject(true);
        ctrlEmp.projectIds.remove(0);
        ctrlEmp.projectIds.add(0, "");
        try {
            Employee empObj = (Employee) jaxbContext.createUnmarshaller().unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", empObj);
            assertTrue("Unmarshal failed:  Employee objects are not equal", ctrlEmp.equals(empObj));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Unmarshal operation failed.");
        }
    }
    
    /**
     * Tests XmlDirectCollectionMapping configuration via eclipselink-oxm.xml.
     * Here a marshal operation is performed.  
     * 
     * Positive test.
     */
    public void testDirectCollectionNullPolicyMarshal() {
        // setup control document
        String src = PATH + "employee-null-projectids.xml";
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }
        try {
            Employee ctrlEmp = getControlObject(true);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(ctrlEmp, testDoc);
            //marshaller.marshal(ctrlEmp, System.out);
            assertTrue("Document comparison failed unxepectedly: ", compareDocuments(ctrlDoc, testDoc));
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("Marshal operation failed.");
        }
    }
}