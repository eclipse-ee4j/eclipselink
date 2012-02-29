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
 *     Matt MacIvor - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmljoinnode.xmlvalue;

import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlJoinNodesWithValueTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE="org/eclipse/persistence/testing/jaxb/annotations/xmljoinnode/xmlvalue/root.xml";
    private static final String JSON_RESOURCE="org/eclipse/persistence/testing/jaxb/annotations/xmljoinnode/xmlvalue/root.json";
    public XmlJoinNodesWithValueTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Root.class, Order.class, Customer.class});
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        jaxbMarshaller.setProperty(JAXBMarshaller.JSON_VALUE_WRAPPER, "value");
        jaxbUnmarshaller.setProperty(JAXBUnmarshaller.JSON_VALUE_WRAPPER, "value");
    }

    public void testJSONMarshalNoValueProperty() throws Exception{
    	JAXBMarshaller m = (JAXBMarshaller) jaxbContext.createMarshaller();
    	m.setProperty(JAXBMarshaller.MEDIA_TYPE, "application/json");
    	try{
    	    m.marshal(getControlObject(), new StringWriter());
    	}catch (JAXBException jbe){
    		assertEquals(((org.eclipse.persistence.exceptions.JAXBException)jbe.getLinkedException()).getErrorCode(), org.eclipse.persistence.exceptions.JAXBException.JSON_VALUE_WRAPPER_REQUIRED);
    		return;
    	}
    	fail("A JAXBException should have occurred.");
    }
    
    @Override
    protected Object getControlObject() {
        Root root = new Root();
        root.customers = new ArrayList<Customer>();
        root.orders = new ArrayList<Order>();
        
        Order order = new Order();
        order.id = "123";
        order.itemId = "456";
        root.orders.add(order);
        
        Customer customer = new Customer();
        customer.info = "value";
        customer.order = order;
        root.customers.add(customer);
        
        return root;
    }

}
