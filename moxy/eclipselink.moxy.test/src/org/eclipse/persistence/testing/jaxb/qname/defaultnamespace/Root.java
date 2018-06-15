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
//     Matt MacIvor - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.qname.defaultnamespace;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;

@XmlRootElement(name="root", namespace="myns")
public class Root {
    public QName qname;

    public List<QName> listOfNames;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Root root = (Root) o;

        if (listOfNames != null ? !listOfNames.equals(root.listOfNames) : root.listOfNames != null) return false;
        if (qname != null ? !qname.equals(root.qname) : root.qname != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = qname != null ? qname.hashCode() : 0;
        result = 31 * result + (listOfNames != null ? listOfNames.hashCode() : 0);
        return result;
    }
}
