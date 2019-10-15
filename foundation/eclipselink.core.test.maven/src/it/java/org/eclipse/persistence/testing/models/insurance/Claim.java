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
package org.eclipse.persistence.testing.models.insurance;

import java.io.*;

/**
 * <p><b>Purpose</b>: Represents an insurance claim on a policy.
 * <p><b>Description</b>: Held in a private 1:M relationship from Policy
 * @see PolicyHolder
 * @since TOPLink/Java 1.0
 */
public abstract class Claim implements Serializable {
    private long id;
    private float amount;
    private Policy policy;

    public Claim() {
        this.amount = (float)0.0;
    }

    public float getAmount() {
        return amount;
    }

    public long getId() {
        return id;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()) + ": " + getAmount();
    }
}
