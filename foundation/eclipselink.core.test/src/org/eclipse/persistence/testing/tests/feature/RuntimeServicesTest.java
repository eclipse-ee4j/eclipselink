/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.feature;

import java.util.*;

import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;

import org.eclipse.persistence.testing.framework.*;
import deprecated.services.*;
import org.eclipse.persistence.testing.models.employee.domain.*;

/**
 * CR3855  Test runtime services.
 */
public class RuntimeServicesTest extends TestCase {
    private RuntimeServices service;
    private boolean notFoundException;
    private boolean castException;
    protected Class identityMapClass, originalIdentityMapClass;
    protected int originalIdentityMapSize;

    public RuntimeServicesTest() {
        setDescription("Verify that getObjectsInIdentityMapSubCache works");
    }

    protected void setup() throws Exception {
        //Make a copy of the original identity map and reset the identity map to HardCacheWeakIdentityMap.
        originalIdentityMapClass = getSession().getDescriptor(Address.class).getIdentityMapClass();
        originalIdentityMapSize = getSession().getDescriptor(Address.class).getIdentityMapSize();
        getSession().getDescriptor(Address.class).setIdentityMapClass(HardCacheWeakIdentityMap.class);
        getSession().getDescriptor(Address.class).setIdentityMapSize(15);
        //Put some addresses in the cache.
        getSession().readAllObjects(Address.class);
        if (!(getSession() instanceof org.eclipse.persistence.sessions.Session)) {
            throw new TestWarningException("This test only operates on sessions of type org.eclipse.persistence.sessions.Session.");
        }
        service = new RuntimeServices((AbstractSession)getSession());
    }

    public void test() {
        try {
            List addresses = 
                service.getObjectsInIdentityMapSubCache("org.eclipse.persistence.testing.models.employee.domain.Address");
        } catch (ClassNotFoundException e) {
            notFoundException = true;
        } catch (ClassCastException e) {
            castException = true;
        }
    }

    protected void verify() {
        if (notFoundException) {
            throw new TestErrorException("RuntimeServices.getObjectsInIdentityMapSubCache could not find the class.");
        }

        if (castException) {
            throw new TestErrorException("RuntimeServices.getObjectsInIdentityMapSubCache could not convert the class.");
        }
    }

    public void reset() {
        getSession().getDescriptor(Address.class).setIdentityMapClass(originalIdentityMapClass);
        getSession().getDescriptor(Address.class).setIdentityMapSize(originalIdentityMapSize);
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
