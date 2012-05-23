/*******************************************************************************
* Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     mmacivor - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.cdata;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import org.w3c.dom.Document;


public class XmlCDATAOverrideTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlcdata/employee_override.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlcdata/employee.json";
    public XmlCDATAOverrideTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }
 
    @Override
    public void setUp() throws Exception {
        super.setUp();
        setTypes(new Class[]{Employee.class});
    }
    
    public Object getControlObject() {
        Employee emp = new Employee();
        emp.name = "Jane Doe";
        emp.xmlData = "<root><child>A string wrapped in cdata</child></root>";
        return emp;
    }
    @Override
    public void testObjectToContentHandler() {
        //CDATA sections don't work with content handlers
    }
    
    protected Map getProperties() {

        String bindings = 
            "<xml-bindings xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence/oxm\"> " +
                "<java-types>" + 
                    "<java-type name=\"org.eclipse.persistence.testing.jaxb.annotations.cdata.Employee\">" + 
                        "<java-attributes>" + 
                            "<xml-element java-attribute=\"xmlData\" cdata=\"false\"/>" + 
                         "</java-attributes>" + 
                   "</java-type>" + 
                "</java-types>" + 
             "</xml-bindings>";

        DOMSource src = null;
        try {             
            Document doc = parser.parse(new ByteArrayInputStream(bindings.getBytes()));
            src = new DOMSource(doc.getDocumentElement());
        } catch (Exception e) {
            e.printStackTrace();
            fail("An error occurred during setup");
        }
            
        HashMap<String, Source> overrides = new HashMap<String, Source>();
        overrides.put("org.eclipse.persistence.testing.jaxb.annotations.cdata", src);
        HashMap properties = new HashMap();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
        return properties;
    }      
}
