/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Praba Vijayaratnam - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.javadoc.xmlelements;

import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlElementWrapper;

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
