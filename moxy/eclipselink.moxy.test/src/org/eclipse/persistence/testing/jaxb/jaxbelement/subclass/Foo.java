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

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class Foo
    extends JAXBElement<Object>
{

    protected final static QName NAME = new QName("", "foo");

    public Foo(Object value) {
        super(NAME, ((Class) Object.class), null, value);
    }

    public Foo() {
        super(NAME, ((Class) Object.class), null, null);
    }

}
