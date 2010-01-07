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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.framework.TestWarningException;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;
import org.eclipse.persistence.testing.models.insurance.PolicyHolder;


public class NoIdentityMergeCloneTest extends TransactionalTestCase {
    protected PolicyHolder objectToBeWritten;
    protected Hashtable checkCacheState;
    protected Hashtable identityMapTypes;

    public NoIdentityMergeCloneTest() {
        setDescription("Test that the unit of work mergeClone still works when object identity is lost.");
    }

    public void reset() {
        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            return;
        }
        super.reset();
        Enumeration enumtr = checkCacheState.keys();
        while (enumtr.hasMoreElements()) {
            ClassDescriptor key = (ClassDescriptor)enumtr.nextElement();
            key.getQueryManager().getDoesExistQuery().setExistencePolicy(((Integer)checkCacheState.get(key)).intValue());
        }
        enumtr = identityMapTypes.keys();
        while (enumtr.hasMoreElements()) {
            ClassDescriptor key = (ClassDescriptor)enumtr.nextElement();
            key.setIdentityMapClass((Class)identityMapTypes.get(key));
        }
    }

    public void setup() {
        if (getSession() instanceof org.eclipse.persistence.sessions.remote.RemoteSession) {
            throw new TestWarningException("Not support in remote");
        }
        super.setup();
        //Have to keep track of what the setting for existence checking and IM types on a per descriptor basis
        // because it may not be the same for all descriptors.
        this.checkCacheState = new Hashtable(10);
        this.identityMapTypes = new Hashtable(10);
        Iterator iterator = getSession().getProject().getDescriptors().values().iterator();
        while (iterator.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)iterator.next();
            checkCacheState.put(descriptor, 
                                new Integer(descriptor.getQueryManager().getDoesExistQuery().getExistencePolicy()));
            if(descriptor.requiresInitialization()) {
                // identityMapClass is null for AggregateObject, AggregateMapping and Interface descriptors.
                identityMapTypes.put(descriptor, descriptor.getIdentityMapClass());
            }
        }
        getSession().getProject().checkDatabaseForDoesExist();
        getSession().getProject().useCacheIdentityMap(1);
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();
    }

    public void test() {
        this.objectToBeWritten = 
                (PolicyHolder)(getSession().readAllObjects(org.eclipse.persistence.testing.models.insurance.PolicyHolder.class)).firstElement();
        this.objectToBeWritten = 
                (PolicyHolder)getSession().acquireUnitOfWork().registerObject(this.objectToBeWritten);
        this.objectToBeWritten.setAddress(null);

        UnitOfWork uow = getSession().acquireUnitOfWork();
        uow.mergeClone(this.objectToBeWritten);
        uow.commit();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        Object objectFromDatabase = getSession().readObject(this.objectToBeWritten);

        if (!(compareObjects(this.objectToBeWritten, objectFromDatabase))) {
            throw new TestErrorException("The object inserted into the database, '" + objectFromDatabase + 
                                         "' does not match the original, '" + this.objectToBeWritten + ".");
        }
    }
}
