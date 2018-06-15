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
//     Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.json.xmlvalue;

import java.util.List;

import javax.xml.bind.annotation.XmlValue;

public class Address {

    @XmlValue
    private List<String> addressInfo;


    public List<String> getAddressInfo() {
        return addressInfo;
    }

    public void setAddressInfo(List<String> addressInfo) {
        this.addressInfo = addressInfo;
    }

    public String toString(){
          String s = "Address:" +  " ";
        for(int i=0; i<getAddressInfo().size(); i++){
            s += " " + getAddressInfo().get(i);
        }

        return s;
    }

    public boolean equals(Object obj) {
        Address add;
        try {
            add = (Address) obj;
        } catch (ClassCastException cce) {
            return false;
        }
       // return city.equals(add.city);// && street.equals(add.street);
        if(addressInfo.size() != add.getAddressInfo().size()){
            return false;
        }

        return addressInfo.containsAll(add.getAddressInfo()) && add.getAddressInfo().containsAll(addressInfo);
    }

}
