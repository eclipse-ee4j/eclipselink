/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.inheritance.typetests;

public class CanadianAddress extends Address {
    private String postalCode;

    public CanadianAddress() {}

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public boolean equals(Object theAddress) {
        if (super.equals(theAddress) && theAddress instanceof CanadianAddress) {
            return postalCode.equals(((CanadianAddress)theAddress).getPostalCode());
        }
        return false;
    }

    public String toString() {
        return super.toString() + " postalCode:" + postalCode;
    }
}
