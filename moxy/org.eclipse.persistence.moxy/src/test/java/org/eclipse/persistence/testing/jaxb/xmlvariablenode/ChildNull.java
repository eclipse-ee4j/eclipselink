/*
 * Copyright (c) 2018, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - May 2018
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import org.eclipse.persistence.oxm.annotations.XmlMarshalNullRepresentation;
import org.eclipse.persistence.oxm.annotations.XmlNullPolicy;

import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlValue;

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
