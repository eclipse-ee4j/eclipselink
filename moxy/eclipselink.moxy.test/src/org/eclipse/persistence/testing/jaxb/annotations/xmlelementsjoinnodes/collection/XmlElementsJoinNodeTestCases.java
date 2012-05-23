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
 * dmccann - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes.collection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes.Address;
import org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes.PhoneNumber;

public class XmlElementsJoinNodeTestCases extends JAXBWithJSONTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementsjoinnodes/root.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlelementsjoinnodes/root.json";
    
    public XmlElementsJoinNodeTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{ Root.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        jaxbMarshaller.setProperty(MarshallerProperties.JSON_VALUE_WRAPPER, "value");
        jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_VALUE_WRAPPER, "value");
    }
    
    public Object getJSONReadControlObject() {
    	   Address address1 = new Address("a100", "123 Some Street", "shipping");
           Address address2 = new Address("a101", "66 Dead End Rd.", "home");
           Address address3 = new Address("a101", "45 O'Connor St.", "work");
           Address address4 = new Address("a101", "101 Metcalfe St.", "billing");
           Address address5 = new Address("a102", "61 McClintock Way", "home");
           
           PhoneNumber phone1 = new PhoneNumber("p100", "613.288.6789", "work");
           PhoneNumber phone2 = new PhoneNumber("p100", "613.858.6789", "cell");
           PhoneNumber phone3 = new PhoneNumber("p101", "613.288.0000", "home");
           PhoneNumber phone4 = new PhoneNumber("p101", "613.420.1212", "work");

           List<Object> clientOneContacts = new ArrayList<Object>();
           clientOneContacts.add(address2);
           clientOneContacts.add(phone3);
           
           List<Object> clientTwoContacts = new ArrayList<Object>();
           clientTwoContacts.add(address5);           
           clientTwoContacts.add(address1);
           clientTwoContacts.add(phone2);
           
           Client client1 = new Client("c100", clientOneContacts);
           Client client2 = new Client("c200", clientTwoContacts);
           
           List<Address> addressList = new ArrayList<Address>();
           addressList.add(address1);
           addressList.add(address2);
           addressList.add(address3);
           addressList.add(address4);
           addressList.add(address5);
           
           List<PhoneNumber> phoneList = new ArrayList<PhoneNumber>();
           phoneList.add(phone1);
           phoneList.add(phone2);
           phoneList.add(phone3);
           phoneList.add(phone4);
           
           List<Client> clients = new ArrayList<Client>();
           clients.add(client1);
           clients.add(client2);
           
           return new Root(clients, addressList, phoneList);	    	
    }
    
    public Object getControlObject() {
        Address address1 = new Address("a100", "123 Some Street", "shipping");
        Address address2 = new Address("a101", "66 Dead End Rd.", "home");
        Address address3 = new Address("a101", "45 O'Connor St.", "work");
        Address address4 = new Address("a101", "101 Metcalfe St.", "billing");
        Address address5 = new Address("a102", "61 McClintock Way", "home");
        
        PhoneNumber phone1 = new PhoneNumber("p100", "613.288.6789", "work");
        PhoneNumber phone2 = new PhoneNumber("p100", "613.858.6789", "cell");
        PhoneNumber phone3 = new PhoneNumber("p101", "613.288.0000", "home");
        PhoneNumber phone4 = new PhoneNumber("p101", "613.420.1212", "work");

        List<Object> clientOneContacts = new ArrayList<Object>();
        clientOneContacts.add(address2);
        clientOneContacts.add(phone3);
        
        List<Object> clientTwoContacts = new ArrayList<Object>();
        clientTwoContacts.add(address5);
        clientTwoContacts.add(phone2);
        clientTwoContacts.add(address1);
        
        Client client1 = new Client("c100", clientOneContacts);
        Client client2 = new Client("c200", clientTwoContacts);
        
        List<Address> addressList = new ArrayList<Address>();
        addressList.add(address1);
        addressList.add(address2);
        addressList.add(address3);
        addressList.add(address4);
        addressList.add(address5);
        
        List<PhoneNumber> phoneList = new ArrayList<PhoneNumber>();
        phoneList.add(phone1);
        phoneList.add(phone2);
        phoneList.add(phone3);
        phoneList.add(phone4);
        
        List<Client> clients = new ArrayList<Client>();
        clients.add(client1);
        clients.add(client2);
        
        return new Root(clients, addressList, phoneList);
    }
}
