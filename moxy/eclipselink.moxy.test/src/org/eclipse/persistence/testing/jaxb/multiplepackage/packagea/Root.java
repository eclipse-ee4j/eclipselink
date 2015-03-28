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
 *     Denise Smith - November 11, 2009
 ******************************************************************************/
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
