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
package org.eclipse.persistence.testing.oxm.descriptor.rootelement;

public class MailingAddress {
    private String street;

    public MailingAddress() {
        super();
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet() {
        return street;
    }

    public boolean equals(Object theObject) {
        if (theObject instanceof MailingAddress) {
            if (((street == null) && (((MailingAddress)theObject).getStreet() == null)) || (street.equals(((MailingAddress)theObject).getStreet()))) {
                return true;
            }
        }
        return false;
    }
}
