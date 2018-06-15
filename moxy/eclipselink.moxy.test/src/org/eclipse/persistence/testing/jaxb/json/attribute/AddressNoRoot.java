/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - August 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.json.attribute;

import javax.xml.bind.annotation.XmlAttribute;

public class AddressNoRoot {

    @XmlAttribute
    private int id;
    private String street;
    @XmlAttribute
    private String city;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String toString(){
        String s = "Address:" +  getStreet() +" " + getCity();
        return s;
    }

    public boolean equals(Object obj) {
        AddressNoRoot add;
        try {
            add = (AddressNoRoot) obj;
        } catch (ClassCastException cce) {
            return false;
        }

        if(id!= add.id){
            return false;
        }
        if(street == null){
            if(add.street != null){
                return false;
            }
        }else if(!street.equals(add.street)){
            return false;
        }
        if(city == null){
            if(add.city != null){
                return false;
            }
        }else if(!city.equals(add.city)){
            return false;
        }
        return true;
    }

}
