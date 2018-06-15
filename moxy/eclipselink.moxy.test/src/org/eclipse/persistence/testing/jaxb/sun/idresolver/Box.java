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
//  - rbarkhouse - 21 October 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.sun.idresolver;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

class Box {

    @XmlElementRefs({
            @XmlElementRef(name = "apple", type = Apple.class),
            @XmlElementRef(name = "appleRef", type = AppleRef.class),
            @XmlElementRef(name = "orange", type = Orange.class),
            @XmlElementRef(name = "orangeRef", type = OrangeRef.class),
            @XmlElementRef(name = "melon", type = Melon.class),
            @XmlElementRef(name = "melonRef", type = MelonRef.class) })
    List<Object> fruits = new ArrayList<Object>();

    @Override
    public String toString() {
        return fruits.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Box)) {
            return false;
        }
        Box b = (Box) obj;

        return this.fruits.equals(b.fruits);
    }

}
