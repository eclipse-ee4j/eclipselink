/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - January 2013
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.noxmlrootelement;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    @XmlElementDecl(name="foo1")
    public JAXBElement<Foo> createFoo1() {
        return null;
    }

    @XmlElementDecl(name="foo2")
    public JAXBElement<Foo> createFoo2() {
        return null;
    }
}
