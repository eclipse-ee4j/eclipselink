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
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelement.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement
@XmlType(propOrder={"firstName", "lastName", "address", "phoneNumbers"})
public class Customer {

	@XmlAttribute
    private int id;	
    private String firstName;
    private String lastName;
    private boolean firstTimeCustomer;
    private List<Integer> orderNumbers;
    @XmlPath(value="contact/address")
    private Address address;
    private List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();

    public Customer(){
        orderNumbers = new ArrayList<Integer>();
        phoneNumbers = new ArrayList<PhoneNumber>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isFirstTimeCustomer() {
        return firstTimeCustomer;
    }

    public void setFirstTimeCustomer(boolean firstTimeCustomer) {
        this.firstTimeCustomer = firstTimeCustomer;
    }

    public List<Integer> getOrderNumbers() {
        return orderNumbers;
    }

    public void setOrderNumbers(List<Integer> orderNumbers) {
        this.orderNumbers = orderNumbers;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @XmlElement(name="phoneNumbers")
    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String toString(){
        String s = "Customer:" +  getId() +" " +getFirstName()+" "  + getLastName() +"\n";
        s += "isFirstTimeCustomer:" + isFirstTimeCustomer() +"\n";
        s += address +"\n";
        for(PhoneNumber p: phoneNumbers){
            s+= p + "\n";
        }
        s+= "OrderNumbers: ";
        for(Integer i: getOrderNumbers()){
            s+= i + ",";
        }
        s+="\n";
        return s;
    }

    public boolean equals(Object obj) {
        Customer c;
        try {
            c = (Customer) obj;
        } catch (ClassCastException cce) {
            return false;
        }

        if(address == null){
            if(c.address != null){
                return false;
            }
        } else if(!address.equals(c.address)){
            return false;
        }

        if(phoneNumbers == null){
            if(c.phoneNumbers != null){
                return false;
            }
        } else if(phoneNumbers.size() != c.phoneNumbers.size()){
            return false;
        }else if(!phoneNumbers.containsAll(c.phoneNumbers)){
            return false;
        }

        return getId()== c.getId() && firstName.equals(c.firstName) && lastName.equals(c.lastName) && firstTimeCustomer == c.firstTimeCustomer;
    }

}