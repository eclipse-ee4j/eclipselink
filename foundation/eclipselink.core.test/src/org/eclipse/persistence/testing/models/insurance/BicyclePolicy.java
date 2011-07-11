/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
