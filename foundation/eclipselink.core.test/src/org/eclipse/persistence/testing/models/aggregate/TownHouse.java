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
package org.eclipse.persistence.testing.models.aggregate;

public class TownHouse extends House {
    public TownHouse() {
        super();
    }

    public static TownHouse example3() {
        TownHouse example3 = new TownHouse();

        Oid insurancePolicyId = new Oid();
        insurancePolicyId.setOid(new Integer(333));
        example3.setInsuranceId(insurancePolicyId);
        example3.setDescriptions("renovated 3-bedroom gardon house");
        example3.setLocation("2236 Baseline Rd");
        return example3;
    }

    public static TownHouse example4() {
        TownHouse example4 = new TownHouse();

        Oid insurancePolicyId = new Oid();
        insurancePolicyId.setOid(new Integer(4444));
        example4.setInsuranceId(insurancePolicyId);
        example4.setDescriptions("two bedroom luxury townhouse");
        example4.setLocation("790C Bank Street");
        return example4;
    }
}
