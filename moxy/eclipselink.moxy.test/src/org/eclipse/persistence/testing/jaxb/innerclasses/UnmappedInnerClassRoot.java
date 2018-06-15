/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.3.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.innerclasses;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class UnmappedInnerClassRoot {

    public String name;

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        UnmappedInnerClassRoot test = (UnmappedInnerClassRoot) obj;
        if(null == name) {
            return null == test.name;
        }
        return name.equals(test.name);
    }

    public class Inner {
    }

}
