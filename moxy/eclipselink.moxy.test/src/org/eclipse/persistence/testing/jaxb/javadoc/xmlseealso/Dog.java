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
//     Praba Vijayaratnam - 2.3 - initial implementation

package org.eclipse.persistence.testing.jaxb.javadoc.xmlseealso;

import javax.xml.bind.annotation.XmlElement;

public class Dog extends Animal {

    @XmlElement
    public String barklevel;

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Dog)) {
            return false;
        }
        Dog dog = (Dog) obj;
        return (dog.name.equals(name) && dog.owner.equals(owner) && dog.barklevel
                .equals(barklevel));
    }
}
