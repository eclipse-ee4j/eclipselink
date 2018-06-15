/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlelementref.nills2;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    @XmlElementDecl(namespace = "NS", name = "optFoo-Root")
    public JAXBElement<OptFoo> createOptFoo(OptFoo value) {
        return new JAXBElement<OptFoo>(new QName("NS", "optFoo-Root"), OptFoo.class, null, value);
    }

    @XmlElementDecl(namespace = "NS", name = "bar", scope = OptFoo.class)
    public JAXBElement<Bar> createOptFooBar(Bar value) {
        return new JAXBElement<Bar>(new QName("NS", "bar"), Bar.class, OptFoo.class, value);
    }

}
