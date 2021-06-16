/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - November 11, 2009
package org.eclipse.persistence.testing.jaxb.multiplepackage.packagea;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="rootA", namespace="namespaceA")
public class ClassA {
    @XmlElement(name="id")
    public int id;

    public String toString(){
        return "ClassA: " + id;
    }

    public boolean equals(Object object) {
        ClassA obj = ((ClassA)object);
        if(obj.id != this.id){
            return false;
        }
        return true;
    }
}
