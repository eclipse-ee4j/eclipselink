/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.xmlelementref;

//@jakarta.xml.bind.annotation.XmlRootElement
public class Foo {
    //@jakarta.xml.bind.annotation.XmlElementRefs({@jakarta.xml.bind.annotation.XmlElementRef(type=Bar.class), @jakarta.xml.bind.annotation.XmlElementRef(type=FooBar.class)})
    //@jakarta.xml.bind.annotation.XmlElementRef(type=Bar.class)
    //@jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter(value=BarAdapter.class)
    public String item;

    public boolean equals(Object o) {
        Foo f;
        try {
            f = (Foo)o;
        } catch (ClassCastException cce) {
            return false;
        }
        return this.item.equals(f.item);
    }
}
