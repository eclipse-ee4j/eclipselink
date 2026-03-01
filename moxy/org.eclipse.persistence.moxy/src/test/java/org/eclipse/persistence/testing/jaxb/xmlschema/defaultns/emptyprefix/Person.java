/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     rbarkhouse - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlschema.defaultns.emptyprefix;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Person {

    public String name;

    @XmlElement(namespace="http://www.example.com/BAR")
    public String title;

    @Override
    public String toString() {
        return "Person:[" + name + ", " + title + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person pObj) {
            return (name.equals(pObj.name) && title.equals(pObj.title));
        }
        return false;
    }

}
