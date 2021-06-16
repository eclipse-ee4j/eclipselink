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
package org.eclipse.persistence.testing.tests.remote;

import org.eclipse.persistence.testing.models.insurance.*;

public class InsuranceRefreshObjectTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    protected PolicyHolder ph;

    public InsuranceRefreshObjectTest() {
        setDescription("Tests refreshing an object with a null 1:1 mapping");
    }

    public void reset() {
        getAbstractSession().rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        getAbstractSession().beginTransaction();
    }

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
