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
// dmccann - December 08/2009 - 2.0 - Initial implementation
// Martin Vojtek - November 14/2014 - 2.6 - Added XmlIDExtension
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschematype;

import org.eclipse.persistence.oxm.annotations.XmlIDExtension;

public class EmployeeWithAnnotation {
    @jakarta.xml.bind.annotation.XmlAttribute
    @jakarta.xml.bind.annotation.XmlID
    @XmlIDExtension
    @jakarta.xml.bind.annotation.XmlSchemaType(name="string")
    public int id;
    @jakarta.xml.bind.annotation.XmlElement
    @jakarta.xml.bind.annotation.XmlSchemaType(name="date")
    public java.util.GregorianCalendar hireDate;
}
