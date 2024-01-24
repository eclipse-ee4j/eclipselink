/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlReadTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformer;
import org.eclipse.persistence.oxm.annotations.XmlWriteTransformers;

@XmlRootElement(name="employee")
public class Employee {
    public String name;

    @XmlReadTransformer(transformerClass = NormalHoursTransformer.class)
    @XmlWriteTransformers({
        @XmlWriteTransformer(transformerClass = StartTimeTransformer.class, xmlPath= "normal-hours/start-time/text()"),
        @XmlWriteTransformer(transformerClass = EndTimeTransformer.class, xmlPath="normal-hours/end-time/text()")
    })
    public String[] normalHours;

    @XmlTransient
    public String getStartTime() {
        return normalHours[0];
    }

    @XmlTransient
    public String getEndTime() {
        return normalHours[1];
    }
    public boolean equals(Object obj) {
        if(!(obj instanceof Employee emp)) {
            return false;
        }
        if(getStartTime() == emp.getStartTime() && getEndTime() == emp.getEndTime()) {
            return true;
        }
        return (getStartTime().equalsIgnoreCase(emp.getStartTime()) && getEndTime().equalsIgnoreCase(emp.getEndTime()) && name.equalsIgnoreCase(emp.name));

    }

}
