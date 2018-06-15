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
package org.eclipse.persistence.testing.models.insurance;

/**
 * <p><b>Purpose</b>: Represents an insurance house claim on a policy.
 * <p><b>Description</b>: Held in a private 1:M relationship from Policy
 * @see Policy
 * @since TOPLink/Java 1.0
 */
public class HouseClaim extends Claim {
    private float area;

    public HouseClaim() {
        this.area = 0;
    }

    /**
     * Return an example claim instance.
     */
    public static HouseClaim example1() {
        HouseClaim houseClaim = new HouseClaim();
        houseClaim.setId(100);
        houseClaim.setArea(2000);
        houseClaim.setAmount(20000);
        return houseClaim;
    }

    /**
     * Return an example claim instance.
     */
    public static HouseClaim example2() {
        HouseClaim houseClaim = new HouseClaim();
        houseClaim.setId(101);
        houseClaim.setArea(2000);
        houseClaim.setAmount(40000);
        return houseClaim;
    }

    /**
     * Return an example claim instance.
     */
    public static HouseClaim example3() {
        HouseClaim houseClaim = new HouseClaim();
        houseClaim.setId(102);
        houseClaim.setArea(2000);
        houseClaim.setAmount(100000);
        return houseClaim;
    }

    /**
     * Get the total area of the house
     */
    public float getArea() {
        return area;
    }

    /**
     * Set the total area of the house
     */
    public void setArea(float area) {
        this.area = area;
    }
}
