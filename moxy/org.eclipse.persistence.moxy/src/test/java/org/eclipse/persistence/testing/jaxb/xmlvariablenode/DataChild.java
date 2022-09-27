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

import jakarta.xml.bind.annotation.XmlTransient;

import javax.xml.namespace.QName;

public class DataChild {

    public DataChild() {
    }

    public DataChild(QName name) {
        this.name = name;
    }

    private QName name;

    @XmlTransient
    public QName getName() {
        return name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DataChild{" +
                "name=" + name +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataChild dataChild = (DataChild) o;
        return name.equals(dataChild.name);
    }
}