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
package org.eclipse.persistence.testing.models.aggregate.nested;

public class Guardian {
    protected String firstName;
    protected String lastName;
    protected MailingAddress mailingAddress;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public MailingAddress getMailingAddress() {
        return mailingAddress;
    }

    public void setFirstName(String param1) {
        firstName = param1;
    }

    public void setLastName(String param1) {
        lastName = param1;
    }

    public void setMailingAddress(MailingAddress param1) {
        mailingAddress = param1;
    }
}
