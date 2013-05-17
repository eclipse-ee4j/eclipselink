/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *    Denise Smith - April 2013
 ******************************************************************************/
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
