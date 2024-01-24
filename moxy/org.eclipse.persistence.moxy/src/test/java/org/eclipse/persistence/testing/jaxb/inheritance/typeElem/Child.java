/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//    Denise Smith - August 2013
package org.eclipse.persistence.testing.jaxb.inheritance.typeElem;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(namespace="theNamespace")
@XmlRootElement(namespace="theNamespace")
public class Child extends Parent {

    @XmlElement
    public String type;

    public boolean equals(Object compareObject){
        if(compareObject instanceof Child){
            if(!super.equals(compareObject)){
                return false;
            }
            if(type == null){
                return ((Child)compareObject).type == null;
            }
            return type.equals(((Child)compareObject).type);
        }
        return false;
    }
}
