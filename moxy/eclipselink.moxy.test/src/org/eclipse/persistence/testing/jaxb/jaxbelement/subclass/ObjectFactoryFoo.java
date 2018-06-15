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
//    Denise Smith - April 2013
package org.eclipse.persistence.testing.jaxb.jaxbelement.subclass;

import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactoryFoo {


    public ObjectFactoryFoo() {
    }
    @XmlElementDecl(namespace = "", name = "foo")
    public Foo createFoo(Object value) {
        return new Foo(value);
    }

}
