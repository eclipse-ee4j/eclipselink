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
package org.eclipse.persistence.testing.tests.directmap;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.models.directmap.DirectMapMappings;

/**
 * Bug 4182377
 * Ensure Serialized converters work correctly with BLOBs and DirectMapMappings
 */
public class DirectMapMappingsSerializedConverterTestCase extends AutoVerifyTestCase {
    public DirectMapMappingsSerializedConverterTestCase() {
        setDescription("Ensure SerializedObjectConverter works with DirectMapMapping.");
    }

    public void setup() {
        beginTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    public void test() {
        // Create a directmapmapping with a few items in it
        UnitOfWork uow = getSession().acquireUnitOfWork();
        DirectMapMappings m1 = new DirectMapMappings();
        m1.blobDirectMap.put(new Integer(1), new Integer(1));
        m1.blobDirectMap.put(new Integer(2), new Integer(2));
        DirectMapMappings maps1 = (DirectMapMappings)uow.registerObject(m1);

        uow.commit();
    }

    public void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        DirectMapMappings maps = (DirectMapMappings)uow.readObject(DirectMapMappings.class);
        if (!maps.blobDirectMap.get(new Integer(1)).equals(new Integer(1))) {
            throw new TestErrorException("The cloned direct map does not maintain the proper type when used with a SerializedObjectConverter.");
        }
    }

    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
