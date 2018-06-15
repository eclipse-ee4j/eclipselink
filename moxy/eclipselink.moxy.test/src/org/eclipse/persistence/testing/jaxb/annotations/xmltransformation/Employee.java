/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmltransformation;

import java.sql.Time;
import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlReadTransformer;
import org.eclipse.persistence.oxm.annotations.XmlTransformation;
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
        if(!(obj instanceof Employee)) {
            return false;
        }
        Employee emp = (Employee)obj;
        if(getStartTime() == emp.getStartTime() && getEndTime() == emp.getEndTime()) {
            return true;
        }
        return (getStartTime().equalsIgnoreCase(emp.getStartTime()) && getEndTime().equalsIgnoreCase(emp.getEndTime()) && name.equalsIgnoreCase(emp.name));

    }

}
