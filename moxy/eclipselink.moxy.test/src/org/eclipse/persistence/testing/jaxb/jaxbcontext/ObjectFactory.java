/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;

@XmlRegistry
public class ObjectFactory {

    @XmlElementDecl(namespace="foons", name="foo")
    public JAXBElement<String> createFoo() {
        return new JAXBElement(new javax.xml.namespace.QName("foons", "foo"), String.class, "");
    }
}
