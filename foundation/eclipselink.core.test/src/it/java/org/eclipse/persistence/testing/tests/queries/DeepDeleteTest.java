/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
