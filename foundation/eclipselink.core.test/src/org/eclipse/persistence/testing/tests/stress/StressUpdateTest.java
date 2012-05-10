/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.stress;

import org.eclipse.persistence.testing.framework.*;

/**
 * Test insert many times.
 */
public class StressUpdateTest extends AutoVerifyTestCase {
    public int stressLevel;

    public StressUpdateTest(int stressLevel) {
        this.stressLevel = stressLevel;
    }

    public void test() {
        Address address = (Address)getSession().readObject(org.eclipse.persistence.testing.tests.stress.Address.class);
        getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("update STRESS_ADDRESS set VERSION = 90000000000 where ADDRESS_ID = " + address.getId()));
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        address = (Address)getSession().readObject(org.eclipse.persistence.testing.tests.stress.Address.class);
        for (int i = 0; i < stressLevel; i++) {
            getDatabaseSession().updateObject(address);
        }
    }
}
