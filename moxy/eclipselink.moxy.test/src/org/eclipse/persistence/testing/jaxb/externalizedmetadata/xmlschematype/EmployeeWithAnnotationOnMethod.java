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
// dmccann - 2.3 - Initial implementation
// Martin Vojtek - 2.6 - Added XmlIDExtension
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.eclipse.persistence.oxm.annotations.XmlIDExtension;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class EmployeeWithAnnotationOnMethod {
    private int id;
    private java.util.GregorianCalendar hireDate;


    @javax.xml.bind.annotation.XmlElement
    @javax.xml.bind.annotation.XmlID
    @XmlIDExtension
    @javax.xml.bind.annotation.XmlSchemaType(name="string")
    public int getId() {
        return id;
    }

    @javax.xml.bind.annotation.XmlElement
    @javax.xml.bind.annotation.XmlSchemaType(name="date")
    public java.util.GregorianCalendar getHireDate() {
        return hireDate;
    }
}
