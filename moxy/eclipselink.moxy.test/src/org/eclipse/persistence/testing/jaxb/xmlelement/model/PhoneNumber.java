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