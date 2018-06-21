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
//              James Sutherland - initial example
package org.eclipse.persistence.testing.models.jpa.performance2;

import javax.persistence.Column;
import javax.persistence.Embeddable;

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
