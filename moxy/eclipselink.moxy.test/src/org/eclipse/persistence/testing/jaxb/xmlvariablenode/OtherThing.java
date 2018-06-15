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

public class OtherThing {

    @XmlAttribute
    public String otherThingName;

    @XmlAttribute
    public String otherThingValue;

    @XmlAttribute
    public Integer otherThingInt;

    public boolean equals(Object obj){
        if(obj instanceof OtherThing){
            return ((otherThingInt == null && ((OtherThing)obj).otherThingInt == null) || (otherThingInt.equals(((OtherThing)obj).otherThingInt)))&&
            otherThingName.equals(((OtherThing)obj).otherThingName) &&
            otherThingValue.equals(((OtherThing)obj).otherThingValue);
        }
        return false;
    }
}
