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
 *     Matt MacIvor - 2.5 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.objectgraph;

import java.util.ArrayList;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.ObjectGraph;
import org.eclipse.persistence.jaxb.Subgraph;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ObjectGraphAttributeTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_attribute.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_attribute_write.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_attribute.json";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/objectgraph/customer_attribute_write.json";

    public ObjectGraphAttributeTestCases(String name) throws Exception {
        super(name);
        this.setClasses(new Class[]{Customer.class, Address.class, PhoneNumber.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);   
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        
        ObjectGraph graph = ((JAXBContext)jaxbContext).createObjectGraph(Customer.class);
        graph.addAttributeNodes("lastName");
        graph.addAttributeNodes("age");
        graph.addAttributeNodes("phoneNumbers");
        Subgraph subGraph = graph.addSubgraph("address", Address.class);
        subGraph.addAttributeNodes("country");
        
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.OBJECT_GRAPH, graph);
        jaxbMarshaller.setProperty(MarshallerProperties.OBJECT_GRAPH, graph);        
    }

    @Override
    protected Object getControlObject() {
        Customer cust = new Customer();
        cust.lastName = "Doe";
        cust.age = "35";
        cust.address = new Address();
        cust.address.country = "Canada";
        cust.phoneNumbers = new ArrayList<PhoneNumber>();
        PhoneNumber pn = new PhoneNumber();
        pn.areaCode = "613";
        pn.number = "123-4567";
        cust.phoneNumbers.add(pn);
        
        pn = new PhoneNumber();
        pn.areaCode = "613";
        pn.number = "345-6789";
        cust.phoneNumbers.add(pn);
       
        return cust;
    }
    
    @Override
    public Object getWriteControlObject() {
        Customer cust = new Customer();
        cust.age = "35";
        cust.firstName = "John";
        cust.lastName = "Doe";
        cust.gender = "Make";
        cust.address = new Address();
        cust.address.city = "Ottawa";
        cust.address.country = "Canada";
        cust.address.street = "123 Fake Street";
        cust.phoneNumbers = new ArrayList<PhoneNumber>();
        PhoneNumber pn = new PhoneNumber();
        pn.areaCode = "613";
        pn.number = "123-4567";
        cust.phoneNumbers.add(pn);
        
        pn = new PhoneNumber();
        pn.areaCode = "613";
        pn.number = "345-6789";
        cust.phoneNumbers.add(pn);
        return cust;
    }
}