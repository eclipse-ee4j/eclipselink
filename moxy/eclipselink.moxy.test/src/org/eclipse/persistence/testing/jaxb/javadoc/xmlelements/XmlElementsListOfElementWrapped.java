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
//     Praba Vijayaratnam - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelements;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlRootElement(name = "employee-data")
public class XmlElementsListOfElementWrapped {
    @XmlElementWrapper(name = "my_wrapper")
    @XmlElements({ @XmlElement(name = "A", type = Integer.class),
            @XmlElement(name = "B", type = Float.class) })
    public List items;

    public boolean equals(Object object) {
        XmlElementsListOfElementWrapped example = ((XmlElementsListOfElementWrapped) object);
        return example.items.size() == this.items.size()
                && example.items.get(0).equals(this.items.get(0));
    }
}
