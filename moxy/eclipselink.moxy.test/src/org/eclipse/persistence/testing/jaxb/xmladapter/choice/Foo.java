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
// Matt MacIvor - July 4th 2011
package org.eclipse.persistence.testing.jaxb.xmladapter.choice;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Foo {

    @XmlElements({
        @XmlElement(name="barA", type=BarA.class),
        @XmlElement(name="single-string", type=String.class)
    })
    public Object singleChoice;

    @XmlElements({
        @XmlElement(name="collection-string", type=String.class),
        @XmlElement(name="barC", type=BarC.class),
        @XmlElement(name="barB", type=BarB.class),
        @XmlElement(name="int", type=Integer.class)
    })

    public List<Object> collectionChoice;


    public boolean equals(Object obj) {
        Foo foo = (Foo)obj;

        return singleChoice.equals(foo.singleChoice) && collectionChoice.equals(foo.collectionChoice);

    }
}
