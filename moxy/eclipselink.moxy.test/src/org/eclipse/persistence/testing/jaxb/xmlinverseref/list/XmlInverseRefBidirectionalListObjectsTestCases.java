/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith, February 2013
package org.eclipse.persistence.testing.jaxb.xmlinverseref.list;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlInverseRefBidirectionalListObjectsTestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bidirectionalListObjects.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bidirectionalListObjects.json";

    public XmlInverseRefBidirectionalListObjectsTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE);
        setControlDocument(XML_RESOURCE);
        setClasses(new Class[]{Person.class});
    }

    @Override
    protected Object getControlObject() {
        Person p = new Person();
        p.name = "theName";
        Address addr = new Address();
        addr.street = "theStreet";

        Person p2 = new Person();
        p2.name ="differentPerson";
        addr.owner = p2;
        //addr.owner = p;

        Address addr2 = new Address();
        addr2.street = "theStreet2";

        Person p3 = new Person();
        p3.name ="anotherPerson";
        addr2.owner = p3;

        //addr2.owner = p;
        p.addrs = new ArrayList<Address>();
        p.addrs.add(addr);
        p.addrs.add(addr2);
        return p;
    }

     public void xmlToObjectTest(Object testObject) throws Exception {
            log("\n**xmlToObjectTest**");
            log("Expected:");
            Object controlObject = getReadControlObject();
            if(null == controlObject) {
                log((String) null);
            } else {
                log(controlObject.toString());
            }
            log("Actual:");
            if(null == testObject) {
                log((String) null);
            } else {
                log(testObject.toString());
            }

            compareObject(testObject);
        }

       public void jsonToObjectTest(Object testObject, Object controlObject) throws Exception {
            if(controlObject == null){
                assertNull(testObject);
                return;
            }

            log("\n**xmlToObjectTest**");
            log("Expected:");
            log(controlObject.toString());
            log("Actual:");
            log(testObject.toString());

            compareObject(testObject);
        }

        private void compareObject(Object testObject){

            assertTrue(testObject instanceof Person);
            Person p = (Person)testObject;
            assertTrue(p.name.equals("theName"));

            List<Address> addresses = p.addrs;
            assertEquals(2, addresses.size());

            //////////
            Address addr = p.addrs.get(0);
            assertTrue(addr.street.equals("theStreet"));
            Person owner = (Person)addr.owner;
            assertTrue("Expected name differentPerson but was " + owner.name , owner.name.equals("differentPerson"));
            assertTrue(owner.addrs.size()==1);
            assertTrue(owner.addrs.get(0).street.equals("theStreet"));
            assertTrue(owner.addrs.get(0).owner == owner);

            Address addr2 = p.addrs.get(1);
            assertTrue(addr2.street.equals("theStreet2"));
            Person owner2 = (Person)addr2.owner;

            assertTrue("Expected name anotherPerson but was " + owner2.name , owner2.name.equals("anotherPerson"));

            assertTrue(owner2.addrs.size() == 1);
            assertTrue(owner2.addrs.get(0).street.equals("theStreet2"));
            assertTrue(owner2.addrs.get(0).owner == owner2);
        }

}
