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
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class XmlElementsJoinNodeTestCases extends JAXBTestCases {
    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/reference/root.xml";
    
    public XmlElementsJoinNodeTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{ Root.class });
        setControlDocument(XML_RESOURCE);
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

        Client client1 = new Client("c100", address2);
        Client client2 = new Client("c200", phone2);

        PublicCompany company1 = new PublicCompany();
        company1.setId(1001); company1.setName("Oracle Corporation"); company1.setStockSymbol("ORCL");
        PublicCompany company2 = new PublicCompany();
        company2.setId(1002); company2.setName("Intel Corporation"); company2.setStockSymbol("INTC");

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

        List<Company> companies = new ArrayList<Company>();
        companies.add(company1);
        companies.add(company2);

        return new Root(clients, addressList, phoneList, companies);
    }
}
