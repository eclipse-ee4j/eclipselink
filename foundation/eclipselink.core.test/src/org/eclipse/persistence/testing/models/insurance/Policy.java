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

import java.util.*;
import java.io.*;

/**
 * <p><b>Purpose</b>: Represents an insurance policy.
 * <p><b>Description</b>: Held in a 1-M from PolicyHolder and has a 1-M to Claim.
 * @see Claim
 * @since TOPLink/Java 1.0
 */
public abstract class Policy implements Serializable {
    private long policyNumber;
    private float maxCoverage;
    private String description;
    private Vector claims;
    private PolicyHolder policyHolder;

    public Policy() {
        this.claims = new Vector();
        this.description = "";
        this.maxCoverage = (float)0.0;
    }

    /**
     * Add the claim.
     * Note that it is important to maintain bi-directional relationships both ways when adding.
     */
    public Claim addClaim(Claim claim) {
        getClaims().addElement(claim);
        claim.setPolicy(this);
        return claim;
    }

    public Vector getClaims() {
        return claims;
    }

    public String getDescription() {
        return description;
    }

    public float getMaxCoverage() {
        return maxCoverage;
    }

    public PolicyHolder getPolicyHolder() {
        return policyHolder;
    }

    public long getPolicyNumber() {
        return policyNumber;
    }

    /**
     * Remove the claim.
     * Note that it is important to maintain bi-directional relationships both ways when removing.
     */
    public Claim removeClaim(Claim claim) {
        getClaims().removeElement(claim);
        claim.setPolicy(null);
        return claim;
    }

    public void setClaims(Vector claims) {
        this.claims = claims;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMaxCoverage(float maxCoverage) {
        this.maxCoverage = maxCoverage;
    }

    public void setPolicyHolder(PolicyHolder policyHolder) {
        this.policyHolder = policyHolder;
    }

    public void setPolicyNumber(long policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String toString() {
        return org.eclipse.persistence.internal.helper.Helper.getShortClassName(getClass()) + ": " + getPolicyNumber();
    }
}
