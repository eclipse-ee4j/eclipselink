/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.insurance;


/**
 * <p><b>Purpose</b>: Represents an insurance vehicle policy.
 * <p><b>Description</b>: Held in a 1-M from PolicyHolder and has a 1-M to Claim.
 * @see Claim
 * @since TOPLink/Java 1.0
 */
public class BicyclePolicy extends VehiclePolicy {
    private String color;

    /**
     * Return an example house policy instance.
     */
    public static BicyclePolicy example1() {
        BicyclePolicy bicyclePolicy = new BicyclePolicy();
        bicyclePolicy.setDescription("Nice bike");
        bicyclePolicy.setPolicyNumber(601);
        bicyclePolicy.setModel("Kharkiv");
        bicyclePolicy.setMaxCoverage(1000);
        bicyclePolicy.setColor("red");
        bicyclePolicy.addClaim(VehicleClaim.example4());
        bicyclePolicy.addClaim(VehicleClaim.example5());
        return bicyclePolicy;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}
