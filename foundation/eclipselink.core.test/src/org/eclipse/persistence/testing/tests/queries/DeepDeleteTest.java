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
package org.eclipse.persistence.testing.tests.queries;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.testing.models.insurance.Claim;

/**
 * Tests cascaded delete on 1-many mapping. cascadeAllParts is used.
 * @author Peter O'Blenis
 * @version 1.0 January 18/99
 */
public class DeepDeleteTest extends AutoVerifyTestCase {
    protected Claim claim;

    /**
     * Constructor
     */
    public DeepDeleteTest() {
    }

    protected void setup() {
        beginTransaction();
        claim = (Claim)getSession().readObject(org.eclipse.persistence.testing.models.insurance.HealthClaim.class);
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
    }

    public void test() {

        /** Create update query */
        DeleteObjectQuery query = new DeleteObjectQuery();
        query.setObject(claim);
        query.cascadeAllParts();

        getSession().executeQuery(query);
    }

    protected void verify() {
        if (getSession().readObject(claim) != null) {
            throw new TestErrorException("The private delete test failed.  The private owned relationship was not deleted");
        }

        if (getSession().readObject(claim.getPolicy()) != null) {
            throw new TestErrorException("The private delete test failed.  The private owned relationship was not deleted");
        }

        if (getSession().readObject(claim.getPolicy().getPolicyHolder()) != null) {
            throw new TestErrorException("The private delete test failed.  The private owned relationship was not deleted");
        }

        if (getSession().readObject(claim.getPolicy().getPolicyHolder().getAddress()) != null) {
            throw new TestErrorException("The private delete test failed.  The private owned relationship was not deleted");
        }
    }
}
