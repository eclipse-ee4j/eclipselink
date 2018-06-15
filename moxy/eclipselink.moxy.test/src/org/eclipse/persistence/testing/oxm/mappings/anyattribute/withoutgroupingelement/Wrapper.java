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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anyattribute.withoutgroupingelement;

import java.util.ArrayList;

public class Wrapper {
    private Root theRoot;

    public Wrapper() {
    }

    public boolean equals(Object object) {
        if (object instanceof Wrapper) {
            return theRoot.equals(((Wrapper)object).getTheRoot());
        }
        return false;
    }

    public void setTheRoot(Root theRoot) {
        this.theRoot = theRoot;
    }

    public Root getTheRoot() {
        return theRoot;
    }
}
