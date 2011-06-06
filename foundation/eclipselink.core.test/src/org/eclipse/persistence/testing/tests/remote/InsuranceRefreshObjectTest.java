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
