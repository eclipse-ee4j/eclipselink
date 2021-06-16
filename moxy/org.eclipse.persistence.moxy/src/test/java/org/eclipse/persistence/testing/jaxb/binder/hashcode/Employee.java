/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Matt MacIvor - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.binder.hashcode;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlMarshalNullRepresentation;
import org.eclipse.persistence.oxm.annotations.XmlNullPolicy;

@XmlRootElement(name="employee")
public class Employee {
    private String name;

    private String age;

    private String address;

    @XmlElement(name="name")
    @XmlNullPolicy(nullRepresentationForXml = XmlMarshalNullRepresentation.ABSENT_NODE)
    public String getName() {

        return name;
    }

    public void setName(String theName) {
        this.name = theName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String theAge) {
        this.age = theAge;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String addr) {
        this.address = addr;
    }

    public int hashCode() {
        int hash = 7;
        if(name != null) {
            hash = hash ^ name.hashCode();
        }
        if(age != null) {
            hash = hash ^ age.hashCode();
        }
        return hash;
    }
}
