/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     desmith - July 2012
package org.eclipse.persistence.testing.jaxb.xmlschematype;

import java.math.BigInteger;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Root {

    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger count;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Root root = (Root) o;

        if (count != null ? !count.equals(root.count) : root.count != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return count != null ? count.hashCode() : 0;
    }
}
