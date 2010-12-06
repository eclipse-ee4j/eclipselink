/*******************************************************************************
 * Copyright (c) 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementsjoinnodes;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlKey;
import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="phone-number")
public class PhoneNumber {
    @XmlAttribute(required=true)
    @XmlPath("@pid")
    @XmlID
    public String id;
    
    @XmlElement
    @XmlPath("text()")
    public String number;

    @XmlAttribute(required=true)
    @XmlPath("@type")
    @XmlKey
    public String type;

    public PhoneNumber() {}
    public PhoneNumber(String id, String number, String type) {
        this.id = id;
        this.number = number;
        this.type = type;
    }
    
    public boolean equals(Object o) {
        PhoneNumber p;
        try {
            p = (PhoneNumber) o;
        } catch (ClassCastException cce) {
            return false;
        }
        try {
            return this.id.equals(p.id) && this.number.equals(p.number) && this.type.equals(p.type);
        } catch (Exception x) {
            return false;
        }
    }
}
