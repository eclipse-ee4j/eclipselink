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
package org.eclipse.persistence.testing.jaxb.xmlinverseref;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlInverseRefObjectsTestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bidirectionalObjects.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bidirectionalObjects.json";

    public XmlInverseRefObjectsTestCases(String name) throws Exception {
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
        p.addr = addr;
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
            Address addr = p.addr;
            assertTrue(addr.street.equals("theStreet"));
            Person owner = (Person)addr.owner;
            assertTrue(owner.name.equals("differentPerson"));
            assertTrue(owner.addr == addr);
        }


}
