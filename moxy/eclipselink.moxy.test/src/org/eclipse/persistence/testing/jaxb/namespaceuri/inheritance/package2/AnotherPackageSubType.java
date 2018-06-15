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
//     Oracle - December 2011
package org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.package2;

import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.testing.jaxb.namespaceuri.inheritance.SubType;

@XmlType(namespace = "uri3")
public class AnotherPackageSubType extends SubType{
    public boolean equals(Object obj){
        if(!(obj instanceof AnotherPackageSubType)){
            return false;
        }
        if(!super.equals(obj)){
            return false;
        }
        return true;
    }
}
