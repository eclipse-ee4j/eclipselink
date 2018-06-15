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
//     bdoughan - Feb 23/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.transformation.element;

public class Root {

    private String element;

    public Root() {
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public boolean equals(Object object) {
        try {
            Root root = (Root) object;
            if(null == root.getElement()) {
                return null == element;
            } else {
                return root.getElement().equals(element);
            }
        } catch(ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        return "Root(element=" + element + ")";
    }
}
