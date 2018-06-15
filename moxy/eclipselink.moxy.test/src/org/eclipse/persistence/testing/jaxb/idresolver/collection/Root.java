/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 29 January 2013 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.idresolver.collection;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Root {

    @XmlAttribute
    public int id;

    @XmlElement(name = "test-object")
    public ArrayList<TestObject> testObjects = new ArrayList<TestObject>();

    @Override
    public String toString() {
        return "Root [id=" + id + ", testObjects=" + testObjects + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result
                + ((testObjects == null) ? 0 : testObjects.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Root other = (Root) obj;
        if (id != other.id)
            return false;
        if (testObjects == null) {
            if (other.testObjects != null)
                return false;
        } else if (!testObjects.equals(other.testObjects))
            return false;
        return true;
    }

}
