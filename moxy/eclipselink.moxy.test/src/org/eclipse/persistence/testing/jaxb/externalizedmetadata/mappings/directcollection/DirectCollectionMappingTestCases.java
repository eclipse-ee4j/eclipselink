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
    private static final String SAL_1 = "123456.78";
    private static final String SAL_2 = "234567.89";
    private static final String PDATA_1 = "This is some private data";
    private static final String PDATA_2 = "This is more private data";
    private static final String CDATA_1 = "<characters>a b c d e f g</characters>";
    private static final String CDATA_2 = "<characters>h i j k l m n</characters>";
    
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
    private Employee getControlObject() {
        List<String> prjIds = new ArrayList<String>();
        prjIds.add(PRJ_ID1);
        prjIds.add(null);
        prjIds.add(PRJ_ID2);
        prjIds.add(PRJ_ID3);
        
        List<Float> sals = new ArrayList<Float>();
        sals.add(Float.valueOf(SAL_1));
        sals.add(Float.valueOf(SAL_2));
        
        List<String> pData = new ArrayList<String>();
        pData.add(PDATA_1);
        pData.add(PDATA_2);

        List<String> cData = new ArrayList<String>();
        cData.add(CDATA_1);
        cData.add(CDATA_2);

        Employee ctrlEmp = new Employee();
        ctrlEmp.id = EMPID;
        ctrlEmp.projectIds = prjIds;
        ctrlEmp.salaries = sals;
        ctrlEmp.privateData = pData;
        ctrlEmp.characterData = cData;
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
     * Instance documents will be validated here as well.
     * 
     * Positive test.
     */
    public void testSchemaGenAndValidation() {
        // validate schemas
        compareSchemas(employeeResolver.schemaFiles.get(EMPTY_NAMESPACE), new File(PATH + "employees.xsd"));
        // validate employee.xml
        String src = PATH + "employee.xml";
        String result = validateAgainstSchema(src, EMPTY_NAMESPACE, employeeResolver);
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
        // validate write-employee.xml
        src = PATH + "write-employee.xml";
        result = validateAgainstSchema(src, EMPTY_NAMESPACE, employeeResolver);
        assertTrue("Instance doc validation (write-employee.xml) failed unxepectedly: " + result, result == null);
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
        // tweak control object
        Employee ctrlEmp = getControlObject();
        // unmarshal null will result in "" being set in the object
        ctrlEmp.projectIds.remove(1);
        ctrlEmp.projectIds.add(1, "");
        // 'privateData' is write only
        ctrlEmp.privateData = null;

        try {
            Employee empObj = (Employee) jaxbContext.createUnmarshaller().unmarshal(iDocStream);
            assertNotNull("Unmarshalled object is null.", empObj);
            assertTrue("Accessor method was not called as expected", empObj.wasSetCalled);
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
    public void testDirectCollectionMarshal() {
        // setup control document
        String src = PATH + "write-employee.xml";
        Document testDoc = parser.newDocument();
        Document ctrlDoc = parser.newDocument();
        try {
            ctrlDoc = getControlDocument(src);
        } catch (Exception e) {
            e.printStackTrace();
            fail("An unexpected exception occurred loading control document [" + src + "].");
        }
        try {
            Employee ctrlEmp = getControlObject();
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
}