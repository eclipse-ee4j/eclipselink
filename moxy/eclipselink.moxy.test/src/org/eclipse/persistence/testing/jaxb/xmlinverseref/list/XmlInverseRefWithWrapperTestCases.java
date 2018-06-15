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
//     Peter Benedikovic, February 2014
package org.eclipse.persistence.testing.jaxb.xmlinverseref.list;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * Written as regression test for bug 426131. It uses the same control docs as XmlInverseRefBidirectionalList2TestCases.
 * The difference is that the inverse reference on collection side is wrapped using @XmlElementWrapper.
 *
 * @author Peter Benedikovic
 *
 */
public class XmlInverseRefWithWrapperTestCases extends JAXBWithJSONTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bidirectionalList2.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlinverseref/bidirectionalList2.json";

    public XmlInverseRefWithWrapperTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE);
        setControlDocument(XML_RESOURCE);
        setClasses(new Class[]{PersonWithWrapper.class});
    }

    @Override
    protected Object getControlObject() {
        PersonWithWrapper p = new PersonWithWrapper();
        p.setName("theName");
        Address addr = new Address();
        addr.street = "theStreet";
        addr.owner = p;

        Address addr2 = new Address();
        addr2.street = "theStreet2";
        addr2.owner = p;
        p.addrs = new ArrayList<Address>();
        p.addrs.add(addr);
        p.addrs.add(addr2);
        return addr;
    }

    @XmlRootElement(name = "person")
    private static class PersonWithWrapper {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @XmlElementWrapper(name = "addresses")
        @XmlInverseReference(mappedBy = "owner")
        @XmlElement
        public List<Address> addrs;

        public boolean equals(Object obj) {
            if (obj instanceof PersonWithWrapper) {
                PersonWithWrapper comparePerson = (PersonWithWrapper) obj;
                if (!name.equals(comparePerson.name)) {
                    return false;
                }
                if (addrs.size() != comparePerson.addrs.size()) {
                    return false;
                }
                for (int i = 0; i < addrs.size(); i++) {
                    Address next = addrs.get(i);
                    Address nextCompare = comparePerson.addrs.get(i);
                    if (!next.equals(nextCompare)) {// || next.owner != this ||
                                                    // nextCompare.owner != this){
                        return false;
                    }
                    /*
                     * if(next.owner == null && nextCompare.owner != null){ return
                     * false; } if(next.owner != null && nextCompare.owner == null){
                     * return false; }
                     */
                    if (next.owner != this || nextCompare.owner != comparePerson) {
                        return false;
                    }

                }
                return true;
            }
            return false;
        }
    }

    @XmlRootElement
    private static class Address {
       public String street;
       @XmlInverseReference(mappedBy ="addrs")
       @XmlElement
       public PersonWithWrapper owner;

       public boolean equals(Object obj){
           if(obj instanceof Address){
               Address compareAddr = (Address)obj;
               if(! street.equals(compareAddr.street)){
                   return false;
               }
               return listContains(owner.addrs, this) && listContains(compareAddr.owner.addrs, compareAddr);
                //calling person.equals will be an infinite loop
           }
           return false;
       }

       private boolean listContains(List theList, Address addr){
           for(int i=0;i<theList.size(); i++){
               if(theList.get(i) == addr){
                   return true;
               }
           }
           return false;
       }
    }

}
