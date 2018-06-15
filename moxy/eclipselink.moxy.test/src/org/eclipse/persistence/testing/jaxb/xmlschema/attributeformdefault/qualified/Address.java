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
// Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.xmlschema.attributeformdefault.qualified;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="address", namespace="myns")
@XmlType(namespace="myns")
public class Address {
    @XmlAttribute (namespace="myns")
    public String street;
    @XmlAttribute
    public String city;

    public boolean equals(Object obj){
        if(obj instanceof Address){
            Address addrObj = (Address)obj;
            if(!street.equals(addrObj.street)){
                return false;
            }
            if(!city.equals(addrObj.city)){
                return false;
            }
            return true;
        }
        return false;
    }
}
