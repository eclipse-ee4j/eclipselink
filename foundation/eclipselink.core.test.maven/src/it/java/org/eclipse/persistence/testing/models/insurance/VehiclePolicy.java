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


/**
 * <p><b>Purpose</b>: Represents an insurance vehicle policy.
 * <p><b>Description</b>: Held in a 1-M from PolicyHolder and has a 1-M to Claim.
 * @see Claim
 * @since TOPLink/Java 1.0
 */
public class VehiclePolicy extends Policy {
    private String model;

    /**
     * Return an example house policy instance.
     */
    public static VehiclePolicy example1() {
        VehiclePolicy vehiclePolicy = new VehiclePolicy();
        vehiclePolicy.setDescription("Nice car.");
        vehiclePolicy.setPolicyNumber(555);
        vehiclePolicy.setModel("Nissan Pathfinder");
        vehiclePolicy.setMaxCoverage(50000);
        return vehiclePolicy;
    }

    /**
     * Return an example house policy instance.
     */
    public static VehiclePolicy example2() {
        VehiclePolicy vehiclePolicy = new VehiclePolicy();
        vehiclePolicy.setPolicyNumber(566);
        vehiclePolicy.setDescription("Nice car.");
        vehiclePolicy.setModel("Toyota Tercel");
        vehiclePolicy.setMaxCoverage(35000);
        vehiclePolicy.addClaim(VehicleClaim.example1());
        vehiclePolicy.addClaim(VehicleClaim.example2());
        return vehiclePolicy;
    }

    /**
     * Return an example house policy instance.
     */
    public static VehiclePolicy example3() {
        VehiclePolicy vehiclePolicy = new VehiclePolicy();
        vehiclePolicy.setPolicyNumber(577);
        vehiclePolicy.setDescription("Nice car.");
        vehiclePolicy.setModel("Toyota Tercel");
        vehiclePolicy.setMaxCoverage(35000);
        vehiclePolicy.addClaim(VehicleClaim.example3());
        return vehiclePolicy;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
