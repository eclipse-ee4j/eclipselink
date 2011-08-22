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
 * dmccann - August 6/2009 - 2.0 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlcustomizer;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.oxm.OXTestCase;

/**
 * Tests XmlCustomizer - this annotation facilitates descriptor customization.
 *
 */
public class XmlCustomizerTestCases extends JAXBWithJSONTestCases {
    
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlcustomizer/employee_no_overrides.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlcustomizer/employee_no_overrides.json";

    /**
     * Test @XmlCustomizer annotation on the Java class.  Here, no XML override
     * is performed.  The instance doc will contain 'first-name' and 'last-name' 
     * tags which were changed by the customizer from 'firstName' and
     * 'lastName' respectively.
     * 
     * Positive test.
     */
    public XmlCustomizerTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Employee.class});
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
    }
    
    public void testSchemaGen() throws Exception{
    	List controlSchemas = new ArrayList();
    	InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlcustomizer/employee.xsd");
    	
    	controlSchemas.add(is);
    	
    	super.testSchemaGen(controlSchemas);
    	
    	
    }
 
    /**
     * Test @XmlCustomizer annotation on the Java class.  Here, no XML override
     * is performed.  The instance doc will contain 'firstName' and 'lastName' 
     * tags which will have been changed by the customizer to 'first-name' and
     * 'last-name' respectively.
     * 
     * Negative test.
     */
    public void testXmlCustomizerNoOverrideFail() {
        Class<?>[] classes = { 
                Employee.class
            };

        JAXBContext jaxbContext = null;
        try {
            jaxbContext = (JAXBContext) JAXBContextFactory.createContext(classes, null);
        } catch (JAXBException e) {
            e.printStackTrace();
            fail("An unexpected exception occurred");
        }

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Marshaller marshaller = jaxbContext.createMarshaller();

        Object obj;
        try {
            // test unmarshal
            obj = unmarshaller.unmarshal(new StreamSource(new StringReader(getInstanceDocumentInvalid())));
            assertFalse("Unmarshal operation succeeded unexpectedly", obj.equals(getControlObject()));

            // test marshal
            StringWriter sw = new StringWriter();
            marshaller.marshal(getControlObject(), sw);

            String ctrlString = OXTestCase.removeWhiteSpaceFromString(getInstanceDocumentInvalid());
            String testString = OXTestCase.removeWhiteSpaceFromString(sw.toString());
            assertFalse("Comparison succeeded unexpectedly", ctrlString.equals(testString));
        } catch (JAXBException e) {
            fail("An unexpected exception occurred");
        }
    }
       
    private String getInstanceDocumentInvalid() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><employee><firstName>Joe</firstName><lastName>Oracle</lastName></employee>";
    }
     
    public Object getControlObject() {
        Employee emp = new Employee();
        emp.firstName = "Joe";
        emp.lastName = "Oracle";
        return emp;
    }
}
