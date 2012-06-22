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
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.ExternalizedMetadataTestCases;
import org.w3c.dom.Document;

public class XmlPathOverrideTestCases extends JAXBTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/xmlpathannotation_override.xml";

    public XmlPathOverrideTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
    }
    @Override
    public void setUp() throws Exception {
        super.setUp();
        setTypes(new Class[]{Employee.class, Address.class, Root.class, PhoneNumber.class});
    }
    
    public Object getControlObject() {
        Employee emp = new Employee();
        emp.id = 101;
        emp.firstName = "Jane";
        emp.lastName = "Doe";
        emp.address = new Address();
        emp.address.street = "123 Fake Street";
        emp.address.city = "Ottawa";
        emp.address.id="102";

        emp.phones = new Vector<PhoneNumber>();
        
        PhoneNumber num1 = new PhoneNumber();
        num1.number = "123-4567";
        emp.phones.add(num1);
        
        PhoneNumber num2 = new PhoneNumber();
        num2.number = "234-5678";
        emp.phones.add(num2);
        
        emp.attributes = new HashMap<QName, String>();
        emp.attributes.put(new QName("attr1"), "value1");
        emp.attributes.put(new QName("http://myns.com/myns", "attr2"), "value2");
        
        Root root = new Root();
        root.employees = new Vector<Employee>();
        root.addresses = new Vector<Address>();
        
        root.employees.add(emp);
        root.addresses.add(emp.address);
        
        return root;
    }
    
    protected Map getProperties() throws Exception{
        
        String bindings = 
            "<xml-bindings xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence/oxm\"> " +
                "<java-types>" + 
                    "<java-type name=\"org.eclipse.persistence.testing.jaxb.annotations.xmlpath.Employee\">" + 
                        "<java-attributes>" + 
                            "<xml-element java-attribute=\"firstName\" xml-path=\"name[1]/text()\"/>" + 
                            "<xml-element java-attribute=\"lastName\" xml-path=\"name[2]/text()\" nillable=\"false\"/>" +
                            "<xml-attribute java-attribute=\"id\" required=\"false\"/>" +
                            "<xml-element java-attribute=\"address\" name=\"address-id\" xml-idref=\"true\"/>" + 
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
        overrides.put("org.eclipse.persistence.testing.jaxb.annotations.xmlpath", src);
        HashMap properties = new HashMap();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
        return properties;
    }    

}
