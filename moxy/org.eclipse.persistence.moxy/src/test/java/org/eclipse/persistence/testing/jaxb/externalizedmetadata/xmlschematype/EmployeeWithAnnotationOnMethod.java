/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - 2.3 - Initial implementation
// Martin Vojtek - 2.6 - Added XmlIDExtension
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematype;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import org.eclipse.persistence.oxm.annotations.XmlIDExtension;

@XmlAccessorType(XmlAccessType.PROPERTY)
public class EmployeeWithAnnotationOnMethod {
    private int id;
    private java.util.GregorianCalendar hireDate;


    @jakarta.xml.bind.annotation.XmlElement
    @jakarta.xml.bind.annotation.XmlID
    @XmlIDExtension
    @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
    public int getId() {
        return id;
    }

    @jakarta.xml.bind.annotation.XmlElement
    @jakarta.xml.bind.annotation.XmlSchemaType(name="date")
    public java.util.GregorianCalendar getHireDate() {
        return hireDate;
    }
}
