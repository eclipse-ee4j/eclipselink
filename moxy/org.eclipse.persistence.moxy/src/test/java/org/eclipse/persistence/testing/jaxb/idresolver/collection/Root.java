/*
 * Copyright (c) 2013, 2021 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 29 January 2013 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.idresolver.collection;

import java.util.ArrayList;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

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
