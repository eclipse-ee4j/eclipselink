/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - 2.3 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.xmlelementref;

//@javax.xml.bind.annotation.XmlRootElement
public class Foo {
    //@javax.xml.bind.annotation.XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(type=Bar.class), @javax.xml.bind.annotation.XmlElementRef(type=FooBar.class)})
    //@javax.xml.bind.annotation.XmlElementRef(type=Bar.class)
    //@javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter(value=BarAdapter.class)
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
