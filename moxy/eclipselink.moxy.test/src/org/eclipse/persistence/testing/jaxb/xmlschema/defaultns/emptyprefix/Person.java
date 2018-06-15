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
//     rbarkhouse - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlschema.defaultns.emptyprefix;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
        if (obj instanceof Person) {
            Person pObj = (Person) obj;
            return (name.equals(pObj.name) && title.equals(pObj.title));
        }
        return false;
    }

}
