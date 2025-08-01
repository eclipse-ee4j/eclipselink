/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.directmap;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.directmap.DirectMapMappings;

/**
 * Bug 4182377
 * Ensure Serialized converters work correctly with BLOBs and DirectMapMappings
 */
public class DirectMapMappingsSerializedConverterTestCase extends AutoVerifyTestCase {
    public DirectMapMappingsSerializedConverterTestCase() {
        setDescription("Ensure SerializedObjectConverter works with DirectMapMapping.");
    }

    @Override
    public void setup() {
        beginTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }

    @Override
    public void test() {
        // Create a directmapmapping with a few items in it
        UnitOfWork uow = getSession().acquireUnitOfWork();
        DirectMapMappings m1 = new DirectMapMappings();
        m1.blobDirectMap.put(1, 1);
        m1.blobDirectMap.put(2, 2);
        DirectMapMappings maps1 = (DirectMapMappings)uow.registerObject(m1);

        uow.commit();
    }

    @Override
    public void verify() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        UnitOfWork uow = getSession().acquireUnitOfWork();
        DirectMapMappings maps = (DirectMapMappings)uow.readObject(DirectMapMappings.class);
        if (!maps.blobDirectMap.get(1).equals(1)) {
            throw new TestErrorException("The cloned direct map does not maintain the proper type when used with a SerializedObjectConverter.");
        }
    }

    @Override
    public void reset() {
        rollbackTransaction();
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
    }
}
