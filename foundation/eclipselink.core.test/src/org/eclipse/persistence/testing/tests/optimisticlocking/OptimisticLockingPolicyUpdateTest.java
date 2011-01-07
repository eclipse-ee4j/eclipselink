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
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.testing.models.optimisticlocking.Guitar;
import org.eclipse.persistence.testing.models.optimisticlocking.RockBand;
import org.eclipse.persistence.testing.models.optimisticlocking.RockMusician;

/**
 * Test the optimistic locking feature by changing the write lock value on the database.
 */
public class OptimisticLockingPolicyUpdateTest extends OptimisticLockingPolicyChangedValueUpdateTest {
    public OptimisticLockingPolicyUpdateTest(Class aClass) {
        super(aClass);
        setDescription("This test verifies that an optimistic lock exception is thrown when the write lock is changed");
    }

    public void guitarTest() {
        //update any field on the DB
        getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("UPDATE MUSICALINSTRUMENT SET L_FIELD = '###' WHERE ID = " + ((Guitar)originalObject).id));
    }

    public void rockBandTest() {
        //update any field on the DB
        getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("UPDATE ROCKBAND SET NAME = 'FRANK' WHERE ID = " + ((RockBand)originalObject).id));
    }

    public void rockMusicianTest() {
        //update any field on the DB
        getSession().executeNonSelectingCall(new org.eclipse.persistence.queries.SQLCall("UPDATE ROCKMUSICIAN SET STAGE_NAME = 'FRANK' WHERE ID = " + ((RockMusician)originalObject).id));
    }
}
