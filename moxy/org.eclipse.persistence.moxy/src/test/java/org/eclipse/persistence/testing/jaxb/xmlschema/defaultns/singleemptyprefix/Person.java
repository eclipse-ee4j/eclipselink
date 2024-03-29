/*
 * Copyright (c) 2016, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.jaxb.xmlschema.defaultns.singleemptyprefix;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyAttribute;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "name" })
@XmlRootElement(name = "person", namespace = "http://www.example.com/FOO")
public class Person {
    @XmlAttribute(name = "name")
    public String name;

    // Trigger Bug #493879
    @XmlAnyAttribute
    Map anyAttributes;

    @Override
    public String toString() {
        return "Person:[" + name + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person pObj) {
            return name.equals(pObj.name);
        }
        return false;
    }

}
