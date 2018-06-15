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
//    Denise Smith - August 2013
package org.eclipse.persistence.testing.jaxb.inheritance.typeElem;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType(namespace="theNamespace")
@XmlRootElement(namespace="theNamespace")
public class Parent {
    public String foo;

    public boolean equals(Object compareObject){
        if(compareObject instanceof Parent){
            if(foo == null){
                return ((Parent)compareObject).foo == null;
            }
            return foo.equals(((Parent)compareObject).foo);
        }
        return false;
    }
}
