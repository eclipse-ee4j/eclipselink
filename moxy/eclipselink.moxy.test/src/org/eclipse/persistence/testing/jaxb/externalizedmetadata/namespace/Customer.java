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
//     Rick Barkhouse - 2.3.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.namespace;

public class Customer {

    public int id;
    public String name;

    public String account;

    @Override
    public String toString() {
        return "Customer: [" + name + "," + account + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;

        if (!(obj instanceof Customer)) return false;

        Customer c = (Customer) obj;

        return (this.name.equals(c.name)) && (this.account.equals(c.account));
    }

}
