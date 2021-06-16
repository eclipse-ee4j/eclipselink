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
//     Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.xmlattribute.unqualified;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(namespace="theNamespace")
@XmlRootElement(name="root", namespace="theNamespace")
public class TestObject {

    @XmlAttribute(namespace="theNamespace")
    public String theTestString;

    @XmlElement(namespace="theNamespace")
    public String theElementTestString;

    public boolean equals(Object obj){
        if(obj instanceof TestObject){
            return theTestString.equals(((TestObject)obj).theTestString) && theElementTestString.equals(((TestObject)obj).theElementTestString);
        }
        return false;
    }
}
