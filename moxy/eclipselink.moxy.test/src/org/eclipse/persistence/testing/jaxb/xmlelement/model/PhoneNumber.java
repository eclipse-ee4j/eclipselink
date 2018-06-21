/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelement.model;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"type", "number"})
public class PhoneNumber {

    private String type;
    private String number;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String toString(){
        String s = "PhoneNumber:" +  getType()+" " + getNumber()+"\n";
        return s;
    }

    public boolean equals(Object obj) {
        PhoneNumber phone;
        try {
            phone = (PhoneNumber) obj;
        } catch (ClassCastException cce) {
            return false;
        }
        return type.equals(phone.type) && number.equals(phone.number);
    }

}
