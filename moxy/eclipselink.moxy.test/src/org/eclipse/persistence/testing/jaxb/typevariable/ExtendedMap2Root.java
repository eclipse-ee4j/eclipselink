/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.typevariable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class ExtendedMap2Root {

    public ExtendedMap2 foo;

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        ExtendedMap2Root test = (ExtendedMap2Root) obj;
        if(!foo.equals(test.foo)) {
            return false;
        }
        return true;
    }

}
