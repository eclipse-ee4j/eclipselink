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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

public class ThingXmlValue {

    @XmlTransient
    public String thingName;
    @XmlValue
    public String thingValue;

    public String getThingName() {
        return thingName;
    }

    @XmlTransient
    public void setThingName(String thingName) {
        this.thingName = thingName;
    }

    public boolean equals(Object obj){
        if(obj instanceof ThingXmlValue){
            return ((thingName == null && ((ThingXmlValue)obj).thingName ==null) || thingName.equals(((ThingXmlValue)obj).thingName)) &&
            thingValue.equals(((ThingXmlValue)obj).thingValue);
        }
        return false;
    }
}
