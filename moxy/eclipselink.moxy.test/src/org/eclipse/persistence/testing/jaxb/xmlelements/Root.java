/*******************************************************************************
 * Copyright (c) 1998, 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - June 14, 2013
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlelements;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "")
@XmlRootElement(name = "root")
public class Root {

    @XmlElements({
        @XmlElement(name = "string", type = String.class),
        @XmlElement(name = "root", type = Root.class)
    })
    protected List<Object> children;

    @XmlAttribute(name = "theName")
    protected String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Root root = (Root) o;

        if (children != null ? !children.equals(root.children) : root.children != null) return false;
        if (name != null ? !name.equals(root.name) : root.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = children != null ? children.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
