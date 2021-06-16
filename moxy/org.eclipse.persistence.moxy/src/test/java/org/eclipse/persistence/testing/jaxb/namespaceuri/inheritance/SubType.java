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
//     Oracle - December 2011
package org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(namespace = "someNamespace")
public class SubType extends BaseType{

    @XmlElement(namespace="uri1")
    public int subTypeProp;

    public boolean equals(Object obj){
        if(!super.equals(obj)){
            return false;
        }

        if(!(obj instanceof SubType)){
            return false;
        }

        if(subTypeProp != ((SubType)obj).subTypeProp){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SubType [subTypeProp=" + subTypeProp + "]";
    }
}
