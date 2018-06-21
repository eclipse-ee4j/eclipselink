/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle
package org.eclipse.persistence.testing.jaxb.xmlaccessortype;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Root {

    public String name;

    private ChildInterface i;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Root root = (Root) o;

        if (i != null ? !i.equals(root.i) : root.i != null) return false;
        if (name != null ? !name.equals(root.name) : root.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (i != null ? i.hashCode() : 0);
        return result;
    }
}
