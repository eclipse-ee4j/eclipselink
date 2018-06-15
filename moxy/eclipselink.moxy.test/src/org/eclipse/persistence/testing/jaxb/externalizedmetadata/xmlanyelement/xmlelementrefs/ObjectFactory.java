/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {
    @XmlElementDecl(name = "a")
    public JAXBElement<Integer> createFooA(Integer i) {
        return new JAXBElement(new QName("a"), Integer.class, i);
    }

    @XmlElementDecl(name = "b")
    public JAXBElement<Integer> createFooB(Integer i) {
        return new JAXBElement(new QName("b"), Integer.class, i);
    }
}
