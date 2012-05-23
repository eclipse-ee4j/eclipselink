/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - 2.3 - Initial implementation
 ******************************************************************************/
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
