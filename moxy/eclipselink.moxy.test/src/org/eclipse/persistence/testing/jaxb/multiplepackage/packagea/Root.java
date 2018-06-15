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
//     Denise Smith - November 11, 2009
package org.eclipse.persistence.testing.jaxb.multiplepackage.packagea;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.jaxb.multiplepackage.packageb.ClassB;

@XmlRootElement(name="theRoot")
public class Root {
    @XmlElement(name="classA")
    public ClassA theClassA;

    @XmlElement(name="classB")
    public ClassB theClassB;

    public String toString(){
        return "ClassA: " + theClassA + " ClassB:" + theClassB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Root root = (Root) o;

        if (theClassA != null ? !theClassA.equals(root.theClassA) : root.theClassA != null) return false;
        if (theClassB != null ? !theClassB.equals(root.theClassB) : root.theClassB != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = theClassA != null ? theClassA.hashCode() : 0;
        result = 31 * result + (theClassB != null ? theClassB.hashCode() : 0);
        return result;
    }
}
