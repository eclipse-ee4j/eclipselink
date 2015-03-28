/*******************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 ******************************************************************************/
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
