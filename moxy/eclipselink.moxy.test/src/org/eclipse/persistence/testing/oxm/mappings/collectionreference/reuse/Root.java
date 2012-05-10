/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     rbarkhouse - 2009-10-07 13:24:58 - initial implementation
 ******************************************************************************/
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