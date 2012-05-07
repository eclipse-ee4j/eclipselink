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
 * Matt MacIvor - 2011 March 21 - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlvirtualaccessmethods.proporder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class PropOrderTestCases extends JAXBWithJSONTestCases {

    public PropOrderTestCases(String name) throws Exception {
        super(name);
        setControlDocument("org/eclipse/persistence/testing/jaxb/xmlvirtualaccessmethods/proporder/customer.xml");
        setControlJSON("org/eclipse/persistence/testing/jaxb/xmlvirtualaccessmethods/proporder/customer.json");
        setTypes(new Class[] {Customer.class, Parent.class, PhoneNumber.class, Address.class});
    }

    
    
    @Override
    protected Map getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        Map<String, Object> overrides = new HashMap<String, Object>();
        overrides.put("org.eclipse.persistence.testing.jaxb.xmlvirtualaccessmethods.proporder", "org/eclipse/persistence/testing/jaxb/xmlvirtualaccessmethods/proporder/binding.xml");
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
        return properties;
    }

    @Override
    protected Object getControlObject() {
        Customer customer = new Customer();
        customer.set("firstName", "Jane");
        customer.set("lastName", "Doe");

        Address billingAddress = new Address();
        billingAddress.setStreet("1 Billing Street");
        customer.setBillingAddress(billingAddress);

        Address shippingAddress = new Address();
        shippingAddress.setStreet("2 Shipping Road");
        customer.set("shippingAddress", shippingAddress);

        List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(2); 
            
        PhoneNumber workPhoneNumber = new PhoneNumber();
        phoneNumbers.add(workPhoneNumber);

        PhoneNumber homePhoneNumber = new PhoneNumber();
        phoneNumbers.add(homePhoneNumber);

        customer.set("phoneNumbers", phoneNumbers);
        return customer;
    }
    
     public String getWriteControlJSONFormatted(){
    	 return "org/eclipse/persistence/testing/jaxb/xmlvirtualaccessmethods/proporder/customer_formatted.json";
     }
     
     public boolean shouldRemoveWhitespaceFromControlDocJSON(){
    	 return false;
     }
    
}