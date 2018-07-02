/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - December 2011
package org.eclipse.persistence.testing.jaxb.inheritance.ns;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root", namespace="rootNamespace")
public class Root {

    public BaseType baseTypeThing;

    public boolean equals(Object obj){
        if(!(obj instanceof Root)){
            return false;
        }
        if(baseTypeThing == null){
            return ((Root)obj).baseTypeThing == null;
        }
        return baseTypeThing.equals(((Root)obj).baseTypeThing);
    }

    @Override
    public int hashCode() {
        return baseTypeThing != null ? baseTypeThing.hashCode() : 0;
    }
}
