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
// dmccann - December 08/2009 - 2.0 - Initial implementation
// Martin Vojtek - November 14/2014 - 2.6 - Added XmlIDExtension
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematype;

import org.eclipse.persistence.oxm.annotations.XmlIDExtension;

public class EmployeeWithAnnotation {
    @javax.xml.bind.annotation.XmlAttribute
    @javax.xml.bind.annotation.XmlID
    @XmlIDExtension
    @javax.xml.bind.annotation.XmlSchemaType(name="string")
    public int id;
    @javax.xml.bind.annotation.XmlElement
    @javax.xml.bind.annotation.XmlSchemaType(name="date")
    public java.util.GregorianCalendar hireDate;
}
