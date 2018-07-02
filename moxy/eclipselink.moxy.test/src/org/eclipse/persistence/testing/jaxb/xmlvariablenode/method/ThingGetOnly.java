/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.xmlvariablenode.method;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class ThingGetOnly {

    private String thingName;
    private String thingValue;


    public ThingGetOnly(){
    }
    public ThingGetOnly(String thingName){
        this.thingName = thingName;
    }

    public String getThingValue() {
        return thingValue;
    }

    @XmlElement
    public void setThingValue(String thingValue) {
        this.thingValue = thingValue;
    }

    @XmlElement
    public String getThingName() {
        return thingName;
    }

    public boolean equals(Object obj){
        if(obj instanceof ThingGetOnly){
            return((thingName == null && ((ThingGetOnly)obj).thingName ==null) ||(thingName.equals(((ThingGetOnly)obj).thingName))) &&
            thingValue.equals(((ThingGetOnly)obj).thingValue);

        }
        return false;
    }
}
