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
 *     Matt MacIvor - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.adapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.Address;

public class CustomerTestCases extends JAXBTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/predicate/adapter/customer.xml";
    private static final String XSD1_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/predicate/adapter/customer.xsd";
    private static final String XSD2_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/predicate/adapter/atomic.xsd";
    private static final String XSD3_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/predicate/adapter/atom.xsd";

    public static String STREET = "somestreet";
    public static String ALT_STREET = "somealternatestreet";
    public static int PHONE_AREA = 613;
    public static int PHONE_NUMBER = 1231234;
    
    public CustomerTestCases(String name) throws Exception {
        super(name);
        this.setClasses(new Class[] {Customer.class});
        this.setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        Customer c = new Customer();
        c.setName("Glen Harveston");
        c.setId("101");

        PhoneNumber p = new PhoneNumber(); p.setAreaCode(404); p.setNumber(8748748);
        c.getPhoneNumbers().add(p);
        PhoneNumber p2 = new PhoneNumber(); p2.setAreaCode(421); p2.setNumber(5551212);
        c.getPhoneNumbers().add(p2);

        p = new PhoneNumber(); p.setAreaCode(PHONE_AREA); p.setNumber(PHONE_NUMBER);
        c.setPhoneNumber(p);
        
        Address add = new Address();
        add.setStreet(STREET);
        c.setAddress(add);
        
        add = new Address();
        add.setStreet(ALT_STREET);
        c.setAltAddress(add);
        
        return c;
    }

    public void testSchemaGen() throws Exception{
        List controlSchemas = new ArrayList();
        InputStream is = ClassLoader.getSystemResourceAsStream(XSD1_RESOURCE);
        InputStream is2 = ClassLoader.getSystemResourceAsStream(XSD2_RESOURCE);
        InputStream is3 = ClassLoader.getSystemResourceAsStream(XSD3_RESOURCE);
        controlSchemas.add(is);
        controlSchemas.add(is2);
        controlSchemas.add(is3);

        super.testSchemaGen(controlSchemas);
    }

}
