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
package org.eclipse.persistence.testing.tests.unitofwork;

import java.util.Vector;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.insurance.Claim;
import org.eclipse.persistence.testing.models.insurance.HealthClaim;
import org.eclipse.persistence.testing.models.insurance.HouseClaim;
import org.eclipse.persistence.testing.models.insurance.PolicyHolder;
import org.eclipse.persistence.testing.models.insurance.VehicleClaim;


public class NoIdentityTest extends TransactionalTestCase {
    protected PolicyHolder objectToBeWritten;

    public NoIdentityTest() {
        setDescription("Test that the unit of work still works when object identity is lost.");
    }

    public void reset() {
        super.reset();

        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getDescriptor(Claim.class).getQueryManager().checkCacheForDoesExist();
            org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getDescriptor(Claim.class).useFullIdentityMap();
            org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getDescriptor(PolicyHolder.class).getQueryManager().checkCacheForDoesExist();
            org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getDescriptor(PolicyHolder.class).useFullIdentityMap();
        }

        getSession().getDescriptor(Claim.class).getQueryManager().checkCacheForDoesExist();
        getSession().getDescriptor(Claim.class).useFullIdentityMap();
        getSession().getDescriptor(PolicyHolder.class).getQueryManager().checkCacheForDoesExist();
        getSession().getDescriptor(PolicyHolder.class).useFullIdentityMap();
    }

    public void setup() {
        super.setup();

        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getDescriptor(Claim.class).getQueryManager().checkDatabaseForDoesExist();
            org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getDescriptor(Claim.class).useCacheIdentityMap();
            org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getDescriptor(PolicyHolder.class).getQueryManager().checkDatabaseForDoesExist();
            org.eclipse.persistence.testing.tests.remote.RemoteModel.getServerSession().getDescriptor(PolicyHolder.class).useCacheIdentityMap();
        }

        getSession().getDescriptor(Claim.class).getQueryManager().checkDatabaseForDoesExist();
        getSession().getDescriptor(HealthClaim.class).getQueryManager().checkDatabaseForDoesExist();
        getSession().getDescriptor(VehicleClaim.class).getQueryManager().checkDatabaseForDoesExist();
        getSession().getDescriptor(HouseClaim.class).getQueryManager().checkDatabaseForDoesExist();
        getSession().getDescriptor(Claim.class).useCacheIdentityMap();
        getSession().getDescriptor(PolicyHolder.class).getQueryManager().checkDatabaseForDoesExist();
        getSession().getDescriptor(PolicyHolder.class).useCacheIdentityMap();
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        Vector claims = getSession().readAllObjects(Claim.class);
        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.registerAllObjects(claims);
        uow.readAllObjects(PolicyHolder.class);
        this.objectToBeWritten = (PolicyHolder)(uow.readAllObjects(PolicyHolder.class)).firstElement();
        this.objectToBeWritten.setAddress(null);
        uow.commit();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
        Object objectFromDatabase = getSession().readObject(this.objectToBeWritten);

        if (!(compareObjects(this.objectToBeWritten, objectFromDatabase))) {
            throw new TestErrorException("The object inserted into the database, '" + objectFromDatabase + 
                                         "' does not match the original, '" + this.objectToBeWritten + ".");
        }
    }
}
