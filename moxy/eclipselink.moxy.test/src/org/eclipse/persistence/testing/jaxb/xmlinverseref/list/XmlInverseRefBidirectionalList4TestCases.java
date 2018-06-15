/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.xmlinverseref.list;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlInverseRefBidirectionalList4TestCases extends XmlInverseRefBidirectionalList2TestCases {

    public XmlInverseRefBidirectionalList4TestCases(String name)throws Exception {
        super(name);
        setClasses(new Class[]{Person2.class});
    }

    @Override
    protected Object getControlObject() {
        Person2 p = new Person2();
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
    private static class Person2 {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @XmlInverseReference(mappedBy = "owner")
        @XmlElement
        public List<Address> addrs;

        public boolean equals(Object obj) {
            if (obj instanceof Person2) {
                Person2 comparePerson = (Person2) obj;
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
       public Person2 owner;

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
