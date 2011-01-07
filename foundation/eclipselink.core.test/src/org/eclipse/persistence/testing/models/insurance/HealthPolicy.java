/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 * <p><b>Purpose</b>: Represents an insurance health policy.
 * <p><b>Description</b>: Held in a 1-M from PolicyHolder and has a 1-M to Claim.
 * @see Claim
 * @since TOPLink/Java 1.0
 */
public class HealthPolicy extends Policy {
    private float coverageRate;

    /**
     * Return an example claim instance.
     */
    public static HealthPolicy example1() {
        HealthPolicy healthPolicy = new HealthPolicy();
        healthPolicy.setPolicyNumber(200);
        healthPolicy.setDescription("Nice body.");
        healthPolicy.setCoverageRate((float)1.5);
        healthPolicy.setMaxCoverage(50000);
        healthPolicy.addClaim(HealthClaim.example1());
        healthPolicy.addClaim(HealthClaim.example2());
        return healthPolicy;
    }

    /**
     * The rate of coverage under the policy
     */
    public float getCoverageRate() {
        return coverageRate;
    }

    /**
     * The rate of coverage under the policy
     */
    public void setCoverageRate(float coverageRate) {
        this.coverageRate = coverageRate;
    }
}
