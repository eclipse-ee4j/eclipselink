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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlvalue;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="phone")
@XmlType(name="phone-number")
public class PhoneNumberWithAtts {

    @XmlTransient
    public String someTransientThing;


    @XmlValue
    public String number;

    @XmlAttribute(name="area-code")
    public String areaCode;

    public boolean equals(Object o) {
        if(!(o instanceof PhoneNumberWithAtts) || o == null) {
            return false;
        } else {
            return ((PhoneNumberWithAtts)o).number.equals(this.number) &&  ((this.areaCode == null && ((PhoneNumberWithAtts)o).areaCode == null) || ((PhoneNumberWithAtts)o).areaCode.equals(this.areaCode));
        }
    }

    public String toString() {
        return "EMPLOYEE< (" + areaCode + ") " + number + ">";
    }
}
