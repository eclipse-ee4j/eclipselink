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
package org.eclipse.persistence.testing.jaxb.multiplepackage.packageb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.jaxb.multiplepackage.packagea.ClassA;

@XmlRootElement(name="rootB", namespace="namespaceB")
public class ClassB {
    @XmlElement(name="id")
    public int id;

    public String toString(){
        return "ClassB: " + id;
    }

    public boolean equals(Object object) {
        ClassB obj = ((ClassB)object);
        if(obj.id != this.id){
            return false;
        }
        return true;
    }
}
