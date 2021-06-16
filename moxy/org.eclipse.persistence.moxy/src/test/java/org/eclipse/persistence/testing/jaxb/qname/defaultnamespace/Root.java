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
//     Matt MacIvor - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.qname.defaultnamespace;

import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
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
