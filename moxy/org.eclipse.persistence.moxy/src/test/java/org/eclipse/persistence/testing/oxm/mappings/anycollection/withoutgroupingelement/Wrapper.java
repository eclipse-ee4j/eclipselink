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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.anycollection.withoutgroupingelement;

import java.util.ArrayList;

public class Wrapper {
    private java.util.List roots;

    public Wrapper() {
        roots = new ArrayList();
    }

    public void setRoots(java.util.List roots) {
        this.roots = roots;
    }

    public java.util.List getRoots() {
        return roots;
    }

    public boolean equals(Object object) {
        if (object instanceof Wrapper) {
            if (roots.size() == ((Wrapper)object).getRoots().size()) {
                for (int i = 0; i < roots.size(); i++) {
                    Object a = roots.get(i);
                    Object b = ((Wrapper)object).getRoots().get(i);
                    if (!a.equals(b)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }
}
