/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  - Denise Smith - September 2013
package org.eclipse.persistence.testing.jaxb.xmladapter.noarg;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    @XmlElementDecl(name="thing")
    @XmlJavaTypeAdapter(MyAdapter.class)
    public JAXBElement<Something> createSomething(Something something) {
        return new JAXBElement<Something> (new QName("thing"), Something.class, something);
    }

    public JAXBElement<SomethingElse> createSomething(SomethingElse somethingElse) {
        return new JAXBElement<SomethingElse> (new QName("thing"), SomethingElse.class, somethingElse);
    }
}
