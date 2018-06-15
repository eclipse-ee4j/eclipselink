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
// mmacivor - June 05/2008 - 1.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.unmarshaller.autodetect;

import javax.xml.bind.annotation.*;
import javax.xml.bind.*;
import javax.xml.namespace.QName;
@XmlRegistry
public class TestObjectFactory {

    @XmlElementDecl(name="root")
    public JAXBElement<String> createRoot() {
        return new JAXBElement<String>(new QName("root"), String.class, "");
    }

    @XmlElementDecl(name="integer-root")
    public JAXBElement<Integer> createIntegerRoot() {
        return new JAXBElement<Integer>(new QName("integer-root"), Integer.class, new Integer(0));
    }

    public EmployeeCollection createEmployeeCollection() {
        return new EmployeeCollection();
    }
}
