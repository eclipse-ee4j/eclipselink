/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Tests XmlChoiceObjectMappings via eclipselink-oxm.xml
 * 
 */
public class XmlPathsTestCases extends JAXBWithJSONTestCases{
    private static final String INT_VAL = "66";
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpaths/employee.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpaths/employee.json";

    /**
     * This is the preferred (and only) constructor.
     * 
     * @param name
     * @throws Exception 
     */
    public XmlPathsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class[]{Employee.class});    
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
    
    
    
    public void testEmployeeSchemaGen() throws Exception {
        // validate the schema
    	List controlSchemas = new ArrayList();
    	controlSchemas.add(getClass().getClassLoader().getResourceAsStream("org/eclipse/persistence/testing/jaxb/annotations/xmlpaths/employee.xsd"));
    	super.testSchemaGen(controlSchemas);
    }
    

    public void testInstanceDocValidation() {
    	InputStream schema = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/annotations/xmlpaths/employee.xsd");        
        StreamSource schemaSource = new StreamSource(schema); 
                
        InputStream instanceDocStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
        String result = validateAgainstSchema(instanceDocStream, schemaSource);        
        assertTrue("Instance doc validation (employee.xml) failed unxepectedly: " + result, result == null);
    }
 
    public void testInvalidXmlPaths() {
        try {
            JAXBContext jaxbCtx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { InvalidEmployee.class }, null);
        } catch (JAXBException e) {
            //e.printStackTrace();
            return;
        }
        fail("The expected exception was not thrown.");
    }
}