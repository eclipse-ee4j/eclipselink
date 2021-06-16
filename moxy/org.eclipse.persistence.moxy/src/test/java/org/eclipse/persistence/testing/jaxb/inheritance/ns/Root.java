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
package org.eclipse.persistence.testing.jaxb.inheritance.ns;

import jakarta.xml.bind.annotation.XmlRootElement;

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
