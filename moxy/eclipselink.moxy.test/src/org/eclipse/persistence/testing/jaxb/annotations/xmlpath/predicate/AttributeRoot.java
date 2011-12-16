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
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="root")
public class AttributeRoot {

    private String address;
    private List<String> phoneNumber = new ArrayList<String>();

    @XmlPath("link[@rel='address']/@href")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @XmlPath("link[@rel='phone-number`]/@href")
    public List<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(List<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        AttributeRoot test= (AttributeRoot) obj;
        if(!equals(address, test.getAddress())) {
            return false;
        }
        if(!equals(phoneNumber, test.getPhoneNumber())) {
            return false;
        }
        return true;
    }

    private boolean equals(String control, String test) {
        if(null == control) {
            return null == test;
        }
        return control.equals(test);
    }

    private boolean equals(List<String> control, List<String> test) {
        if(null == control) {
            return null == test;
        }
        if(control.size() != test.size()) {
            return false;
        }
        for(int x=0; x<control.size(); x++) {
            if(!equals(control.get(x), test.get(x))) {
                return false;
            }
        }
        return true;
    }

}
