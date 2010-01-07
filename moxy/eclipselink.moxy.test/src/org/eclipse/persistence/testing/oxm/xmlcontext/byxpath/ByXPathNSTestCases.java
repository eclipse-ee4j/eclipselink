/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - August 25/2009 - 1.2 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.xmlcontext.byxpath;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.testing.oxm.OXTestCase;

 public class ByXPathNSTestCases extends OXTestCase {

     private static final String CONTROL_CUSTOMER_ID = "123";
     private static final String CONTROL_CUSTOMER_FIRST_NAME = "Jane";
     private static final String CONTROL_ADDRESS_STREET1 = "Apt. 1";
     private static final String CONTROL_ADDRESS_STREET2 = "123 Any Street";
     private static final String CONTROL_ADDRESS_CITY = "Ottawa";
     private static final String CONTROL_PHONE_NUMBER_1 = "613-555-1111";
     private static final String CONTROL_PHONE_NUMBER_2 = "613-555-2222";
     private static final String CONTROL_EMERGENCY_CONTACT_NAME = "John Doe";

     private XMLContext xmlContext;
     private NamespaceResolver namespaceResolver;

     public ByXPathNSTestCases(String name) {
         super(name);
         namespaceResolver = new NamespaceResolver();
         namespaceResolver.put("a", "urn:customer");
     }

     public void setUp() {
         xmlContext = getXMLContext(new CustomerNSProject());
     }

     public void testGetAttributeValueByXPath() {
          Customer customer = new Customer();
          customer.setId(CONTROL_CUSTOMER_ID);
          String testValue = xmlContext.getValueByXPath(customer, "@a:id", namespaceResolver, String.class);
          assertEquals(CONTROL_CUSTOMER_ID, testValue);
     }

     public void testSetAttributeValueByXPath() {
         Customer customer = new Customer();
         xmlContext.setValueByXPath(customer, "@a:id", namespaceResolver, CONTROL_CUSTOMER_ID);
         assertEquals(CONTROL_CUSTOMER_ID, customer.getId());
     }

     public void testGetTextValueByXPath() {
         Customer customer = new Customer();
         customer.setFirstName(CONTROL_CUSTOMER_FIRST_NAME);
         String testValue = xmlContext.getValueByXPath(customer, "a:personal-info/a:first-name/text()", namespaceResolver, String.class);
         assertEquals(CONTROL_CUSTOMER_FIRST_NAME, testValue);
    }

     public void testSetTextValueByXPath() {
         Customer customer = new Customer();
         xmlContext.setValueByXPath(customer, "a:personal-info/a:first-name/text()", namespaceResolver, CONTROL_CUSTOMER_FIRST_NAME);
         assertEquals(CONTROL_CUSTOMER_FIRST_NAME, customer.getFirstName());
     }

     public void testGetNestedTextValueByXPath() {
         Customer customer = new Customer();
         Address address = new Address();
         address.setCity(CONTROL_ADDRESS_CITY);
         customer.setAddress(address);
         String testValue = xmlContext.getValueByXPath(customer, "a:contact-info/a:address/a:city/text()", namespaceResolver, String.class);
         assertEquals(CONTROL_ADDRESS_CITY, testValue);
     }

     public void testSetNestedTextValueByXPath() {
         Customer customer = new Customer();
         Address address = new Address();
         customer.setAddress(address);
         xmlContext.setValueByXPath(customer, "a:contact-info/a:address/a:city/text()", namespaceResolver, CONTROL_ADDRESS_CITY);
         assertEquals(CONTROL_ADDRESS_CITY, customer.getAddress().getCity());
     }

     public void testGetCollectionValueByXPath() {
         Customer customer = new Customer();
         PhoneNumber phoneNumber = new PhoneNumber();
         customer.getPhoneNumbers().add(phoneNumber);
         List testValue = xmlContext.getValueByXPath(customer, "a:contact-info/a:phone-number/", namespaceResolver, List.class);
         assertSame(customer.getPhoneNumbers(), testValue);
     }

     public void testSetCollectionValueByXPath() {
         Customer customer = new Customer();
         List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>(1);
         phoneNumbers.add(new PhoneNumber());
         xmlContext.setValueByXPath(customer, "a:contact-info/a:phone-number/", namespaceResolver, phoneNumbers);
         assertSame(customer.getPhoneNumbers(), phoneNumbers);
     }

     public void testGetCollectionItemByXPath() {
         Customer customer = new Customer();
         PhoneNumber pn1 = new PhoneNumber();
         customer.getPhoneNumbers().add(pn1);
         PhoneNumber pn2 = new PhoneNumber();
         customer.getPhoneNumbers().add(pn2);

         PhoneNumber testValue = xmlContext.getValueByXPath(customer, "a:contact-info/a:phone-number[2]", namespaceResolver, PhoneNumber.class);
         assertSame(pn2, testValue);
     }

     public void testSetCollectionItemByXPath() {
         Customer customer = new Customer();
         PhoneNumber pn1 = new PhoneNumber();
         customer.getPhoneNumbers().add(pn1);
         PhoneNumber pn2 = new PhoneNumber();
         customer.getPhoneNumbers().add(pn2);
         
         PhoneNumber newPN2 = new PhoneNumber();
         xmlContext.setValueByXPath(customer, "a:contact-info/a:phone-number[2]", namespaceResolver, newPN2);
         assertSame(pn1, customer.getPhoneNumbers().get(0));
         assertSame(newPN2, customer.getPhoneNumbers().get(1));
         assertSame(pn2, customer.getPhoneNumbers().get(2));
     }

     public void testGetViaPositionInListXPath() {
         Customer customer = new Customer();
         PhoneNumber pn1 = new PhoneNumber();
         customer.getPhoneNumbers().add(pn1);
         PhoneNumber pn2 = new PhoneNumber();
         pn2.setValue(CONTROL_PHONE_NUMBER_2);
         customer.getPhoneNumbers().add(pn2);

         String testValue = xmlContext.getValueByXPath(customer, "a:contact-info/a:phone-number[2]/text()", namespaceResolver, String.class);
         assertSame(CONTROL_PHONE_NUMBER_2, testValue);
     }

     public void testSetViaPositionInListXPath() {
         Customer customer = new Customer();
         PhoneNumber pn1 = new PhoneNumber();
         customer.getPhoneNumbers().add(pn1);
         PhoneNumber pn2 = new PhoneNumber();
         customer.getPhoneNumbers().add(pn2);

         xmlContext.setValueByXPath(customer, "a:contact-info/a:phone-number[2]/text()", namespaceResolver, CONTROL_PHONE_NUMBER_2);
         assertSame(CONTROL_PHONE_NUMBER_2, pn2.getValue());
     }

     public void testGetByPositionalXPath() {
         Customer customer = new Customer();
         Address address = new Address();
         address.setStreet2(CONTROL_ADDRESS_STREET2);
         customer.setAddress(address);
         String testValue = xmlContext.getValueByXPath(customer, "a:contact-info/a:address/a:street[2]/text()", namespaceResolver, String.class);
         assertEquals(CONTROL_ADDRESS_STREET2, testValue);
     }

     public void testSetByPositionalXPath() {
         Customer customer = new Customer();
         Address address = new Address();
         customer.setAddress(address);
         xmlContext.setValueByXPath(customer, "a:contact-info/a:address/a:street[2]/text()", namespaceResolver, CONTROL_ADDRESS_STREET2);
         assertEquals(CONTROL_ADDRESS_STREET2, address.getStreet2());
     }

}
