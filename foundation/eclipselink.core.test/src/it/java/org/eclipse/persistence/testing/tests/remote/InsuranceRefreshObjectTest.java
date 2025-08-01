/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.testing.models.insurance.PolicyHolder;

public class InsuranceRefreshObjectTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    protected PolicyHolder ph;

    public InsuranceRefreshObjectTest() {
        setDescription("Tests refreshing an object with a null 1:1 mapping");
    }

    @Override
    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    @Override
    public void setup() {
        getAbstractSession().beginTransaction();
    }

    @Override
    public void test() {
        PolicyHolder policyHolder = new PolicyHolder();
        policyHolder.setFirstName("Matthew");
        policyHolder.setLastName("MacIvor");
        policyHolder.setAddress(null);
        getAbstractSession().writeObject(policyHolder);
        ph =
 (PolicyHolder)getSession().readObject(PolicyHolder.class, new org.eclipse.persistence.expressions.ExpressionBuilder().get("lastName").equal("MacIvor"));
        getSession().refreshObject(ph);
    }
}
