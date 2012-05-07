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
 * dmccann - April 01/2010 - 2.1 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.reference.Address;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.reference.Client;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.reference.PhoneNumber;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.reference.Root;

public class ChoiceMappingWithJoinNodesTestCases extends JAXBWithJSONTestCases{

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/reference/root.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/reference/root.json";
	
	public ChoiceMappingWithJoinNodesTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);
		setControlJSON(JSON_RESOURCE);
		setClasses(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.reference.Root.class });
		jaxbMarshaller.setProperty(MarshallerProperties.JSON_VALUE_WRAPPER, "value");
		jaxbUnmarshaller.setProperty(UnmarshallerProperties.JSON_VALUE_WRAPPER, "value");
	}

	protected Object getControlObject() {
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
          
          Root ctrlRoot = new Root(clients, addressList, phoneList);
          return ctrlRoot;
	}

	public Map getProperties(){
		Map<String, Object> props = new HashMap<String, Object>();
		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/reference/root-oxm.xml");
         
        props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, inputStream);
        return props;
	}
	
    /**
     * Tests exception handling for wrong number of XmlJoinNodes in an XnlElements.
     * 
     * Expects:
     * 
     * "Exception Description: Property [preferredContactMethod] on class [Client] 
     * has an XmlElements declaration containing an unequal amount of XmlElement/XmlJoinNodes.  
     * It is required that there be a corresponding XmlJoinNodes for each XmlElement 
     * contained within the XmlElements declaration."
     * 
     * Negative test.
     */
    public void testIncorrectNumberOfXmlJoinNodes() {
        try {
            Map<String, Object> props = new HashMap<String, Object>();
    		InputStream inputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/externalizedmetadata/mappings/choice/reference/root-invalid-oxm.xml");
            props.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, inputStream);
            JAXBContext ctx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { org.eclipse.persistence.testing.jaxb.externalizedmetadata.mappings.choice.reference.Root.class }, props);
        } catch (JAXBException e1) {
            return;
        }
        fail("The expected exception was never thrown.");
    }
}
