/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      rbarkhouse - 2013 June 24 - 2.5.1 - Initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbcontext.notext;

public class Root {

    public Object rootProp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Root root = (Root) o;

        if (rootProp != null ? !rootProp.equals(root.rootProp) : root.rootProp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return rootProp != null ? rootProp.hashCode() : 0;
    }
}
