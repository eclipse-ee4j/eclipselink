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
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;

public class Thing {

    @XmlElement
    public String thingName;
    public String thingValue;

    public Integer thingInt;

    @XmlTransient
    public QName thingQName;

    public String getThingName() {
        return thingName;
    }

    public void setThingName(String thingName) {
        this.thingName = thingName;
    }

    public boolean equals(Object obj){
        if(obj instanceof Thing){
            return ((thingInt == null && ((Thing)obj).thingInt == null) || (thingInt.equals(((Thing)obj).thingInt)))&&
            thingName.equals(((Thing)obj).thingName) &&
            thingValue.equals(((Thing)obj).thingValue) &&
            ((thingQName == null && ((Thing)obj).thingQName ==null)|| (thingQName.equals(((Thing)obj).thingQName)));
        }
        return false;
    }
}
