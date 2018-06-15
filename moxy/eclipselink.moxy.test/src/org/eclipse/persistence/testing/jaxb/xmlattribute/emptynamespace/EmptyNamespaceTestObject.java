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
//     Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.xmlattribute.emptynamespace;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="theNamespace")
@XmlRootElement(name="root", namespace="theNamespace")
public class EmptyNamespaceTestObject {

    @XmlAttribute(namespace="")
    public String theTestString;

    @XmlElement(namespace="")
    public String theElementTestString;

    public boolean equals(Object obj){
        if(obj instanceof EmptyNamespaceTestObject){
            return theTestString.equals(((EmptyNamespaceTestObject)obj).theTestString) && theElementTestString.equals(((EmptyNamespaceTestObject)obj).theElementTestString);
        }
        return false;
    }
}
