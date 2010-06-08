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
package org.eclipse.persistence.testing.tests.directmap;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.directmap.DirectMapMappings;
import org.eclipse.persistence.testing.models.directmap.IndirectMapSubclass;

/**
 * Bug 3945357
 * Ensure transparent indirection works with DirectMapMappins
 */
public class DirectMapMappingIndirectionTest extends AutoVerifyTestCase {
    protected DirectMapMappings resultMapping = null;

    public DirectMapMappingIndirectionTest() {
        setDescription("Tests that objects deleted from the properties object in a DirectMapMapping are properly removed.");
    }

    public void reset() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        rollbackTransaction();
    }

    public void setup() throws Exception {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        beginTransaction();
    }

    public void test() throws Exception {
        // Create a directmapmapping with a few items in it
        UnitOfWork uow = getSession().acquireUnitOfWork();
        DirectMapMappings maps1 = (DirectMapMappings)uow.registerObject(new DirectMapMappings());
        maps1.indirectionDirectMap.put(new Integer(1), "guy");
        maps1.indirectionDirectMap.put(new Integer(2), "axemen");
        uow.commit();

        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

        resultMapping = (DirectMapMappings)getSession().readObject(maps1);

    }

    public void verify() throws Exception {
        if (!(resultMapping.indirectionDirectMap.getClass() == IndirectMapSubclass.class)) {
            throw new TestErrorException("DirectMap with transparent indirection does not properly use IndirectMap");
        }
    }
}
