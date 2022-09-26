/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - Sep 2022 - - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import jakarta.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.oxm.annotations.XmlVariableNode;

@XmlRootElement(name = "data", namespace = "uri1:")
public class Data {

    public Data() {
    }

    private DataChild child;

    @XmlVariableNode("name")
    public DataChild getChild() {
        return child;
    }

    public void setChild(DataChild child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return "Data{" +
                "child=" + child +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return child.equals(data.child);
    }
}