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
// mmacivor - June 05/2008 - 1.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.*;
import javax.xml.namespace.QName;
@XmlRegistry
public class TestObjectFactory {

    @XmlElementDecl(name="root")
    public JAXBElement<String> createRoot() {
        return new JAXBElement<String>(new QName("root"), String.class, "");
    }

    @XmlElementDecl(namespace="myns", name="integer-root")
    public JAXBElement<Integer> createIntegerRoot() {
        return new JAXBElement<Integer>(new QName("myns", "integer-root"), Integer.class, new Integer(0));
    }

    public EmployeeSingle createEmployeeSingle() {
        return new EmployeeSingle();
    }

    public EmployeeCollection createEmployeeCollection() {
        return new EmployeeCollection();
    }
}
