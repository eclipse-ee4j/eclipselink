/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// Oracle = 2.2 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.choicecollection.ref;

public class Address {

    public String id;

    public String street;

    public String zip;

    public boolean equals(Object obj) {
        Address addr;
        try {
            addr = (Address)obj;
        } catch (Exception ex) {
            return false;
        }

        return id.equals(addr.id)&& street.equals(addr.street) && zip.equals(addr.zip);
    }
}
