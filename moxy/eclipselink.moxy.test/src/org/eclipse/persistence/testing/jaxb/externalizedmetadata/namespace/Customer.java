/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Rick Barkhouse - 2.3.1 - initial implementation
 ******************************************************************************/
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