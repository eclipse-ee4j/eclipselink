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
package org.eclipse.persistence.testing.tests.mapping;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.mapping.EmergencyExit;
import org.eclipse.persistence.testing.models.mapping.Cubicle;

public class AddObjectNonPrimaryKeyManyToManyTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public Cubicle cubicle;

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void setup() {
        if (getSession().getLogin().getPlatform().getDefaultSequence().shouldAcquireValueAfterInsert()) {
            throw new TestWarningException("This test doesn't work with 'after-insert' native sequencing");
        }
        beginTransaction();
    }

    public void test() {
        try {
            UnitOfWork uow = getSession().acquireUnitOfWork();
            cubicle = (Cubicle)uow.readObject(Cubicle.class);
            cubicle.emergencyExits.add(EmergencyExit.example1());
            uow.commit();
        } catch (DatabaseException exception) {
            throw new TestErrorException("CR 3819, Sent Null to database when source foreign key for many to many is not a primary key " + exception.toString());
        }
    }

    public void verify() {
    }
}
