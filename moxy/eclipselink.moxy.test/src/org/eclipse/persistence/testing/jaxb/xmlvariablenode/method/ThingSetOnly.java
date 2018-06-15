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
package org.eclipse.persistence.testing.jaxb.xmlvariablenode.method;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class ThingSetOnly {

    private String thingName;
    private String thingValue;

    public ThingSetOnly(){
    }


    public String getThingValue() {
        return thingValue;
    }

    @XmlElement
    public void setThingValue(String thingValue) {
        this.thingValue = thingValue;
    }

    @XmlElement
    public void setThingName(String thingName) {
        this.thingName = thingName;
    }

    public boolean equals(Object obj){
        if(obj instanceof ThingSetOnly){
            return thingName.equals(((ThingSetOnly)obj).thingName) &&
            thingValue.equals(((ThingSetOnly)obj).thingValue);

        }
        return false;
    }
}
