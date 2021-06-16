/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlValue;
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
