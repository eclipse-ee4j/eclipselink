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
//              James Sutherland - initial example
package org.eclipse.persistence.testing.models.jpa.performance2;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import org.eclipse.persistence.annotations.ChangeTracking;
import org.eclipse.persistence.annotations.ChangeTrackingType;

/**
 * Represents the email address of an employee.
 */
@Embeddable
// TODO, this is currently required to workaround an issue with weaving and ElementCollection mappings.
@ChangeTracking(ChangeTrackingType.DEFERRED)
public class EmailAddress {

    @Column(name = "EMAIL_ADDRESS")
    private String address;

    public EmailAddress() {
    }

    public EmailAddress(String address) {
        setAddress(address);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
