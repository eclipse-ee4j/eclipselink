/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Radek Felcman - May 2018
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import org.eclipse.persistence.oxm.annotations.XmlMarshalNullRepresentation;
import org.eclipse.persistence.oxm.annotations.XmlNullPolicy;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;

public class ChildNull {

    @XmlTransient
    public String key;

    @XmlValue
    @XmlNullPolicy(nullRepresentationForXml= XmlMarshalNullRepresentation.XSI_NIL, xsiNilRepresentsNull=false)
    private String value;

    public ChildNull() {
    }

    public ChildNull(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj){
       if(obj instanceof ChildNull){
           return ((value == null && ((ChildNull)obj).value == null) || (value.equals(((ChildNull)obj).value)));
       }
       return false;
    }
}
