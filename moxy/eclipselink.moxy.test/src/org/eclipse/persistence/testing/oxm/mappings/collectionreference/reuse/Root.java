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
//     rbarkhouse - 2009-10-07 13:24:58 - initial implementation
package org.eclipse.persistence.testing.oxm.mappings.collectionreference.reuse;

import java.util.Collection;

public class Root {

    public Employee employee;
    public Collection addresses;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Root) {
            Root rootObj = (Root) obj;
            if (!(this.employee.equals(rootObj.employee))) {
                return false;
            }
            if (!(this.addresses.equals(rootObj.addresses))) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Root: " + employee.toString();
    }

}
