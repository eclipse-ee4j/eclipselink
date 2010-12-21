/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 * <p><b>Purpose</b>: Represents an insurance health claim on a policy.
 * <p><b>Description</b>: Held in a private 1:M relationship from Policy
 * @see Policy
 * @since TOPLink/Java 1.0
 */
public class HealthClaim extends Claim {
    private String disease;

    public HealthClaim() {
        this.disease = "";
    }

    /**
     * Return an example claim instance.
     */
    public static HealthClaim example1() {
        HealthClaim healthClaim = new HealthClaim();
        healthClaim.setId(200);
        healthClaim.setDisease("Flu");
        healthClaim.setAmount(1000);
        return healthClaim;
    }

    /**
     * Return an example claim instance.
     */
    public static HealthClaim example2() {
        HealthClaim healthClaim = new HealthClaim();
        healthClaim.setId(201);
        healthClaim.setDisease("TB");
        healthClaim.setAmount(7000);
        return healthClaim;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }
}
