/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// Matt MacIvor - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlinversereference;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

@XmlRootElement
@XmlType(propOrder = {"name"})
public class Child {
    public String name;

    @XmlInverseReference(mappedBy="children")
    public Parent parent;

    public boolean equals(Object obj) {
        Child child = (Child)obj;
        if(name == null){
            if(child.name!=null){
                return false;
            }
        }else if(!name.equals(child.name)){
            return false;
        }
        return this.parent != null && child.parent != null;
    }
}
