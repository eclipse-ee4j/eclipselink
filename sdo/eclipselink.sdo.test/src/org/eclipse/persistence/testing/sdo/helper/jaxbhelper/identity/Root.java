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
//     bdoughan - Feb 3/2009 - 1.1 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.identity;

public class Root {

    private String name;

    public Root() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object object) {
        try {
            Root root = (Root) object;
            if(name == null) {
                return null == root.getName();
            } else {
                return name.equals(root.getName());
            }
        } catch(ClassCastException e) {
            return false;
        }
    }

    public int hashCode() {
        return 7;
    }

}
