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
// mmacivor - June 05/2008 - 1.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelementref;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.JAXBElement;

@XmlRootElement(name="employee-single")
public class EmployeeSingle {

    @XmlElementRef(name="integer-root", namespace="myns")
    public JAXBElement<Integer> intRoot;

    public boolean equals(Object emp) {
        JAXBElement root1 = this.intRoot;
        JAXBElement root2 = ((EmployeeSingle)emp).intRoot;

        return root1.getName().equals(root2.getName()) && root1.getValue().equals(root2.getValue()) && root1.getDeclaredType() == root2.getDeclaredType();
    }
}
