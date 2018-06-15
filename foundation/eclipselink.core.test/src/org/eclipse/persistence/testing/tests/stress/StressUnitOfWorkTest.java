/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.stress;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.sessions.*;

/**
 * Test insert many times.
 */

public class StressUnitOfWorkTest extends AutoVerifyTestCase {
    public int stressLevel;
public StressUnitOfWorkTest(int stressLevel)
{
    this.stressLevel = stressLevel;
}
public void test( )
{
    for (int i = 0; i < stressLevel; i++) {
        UnitOfWork unitofWork = getSession().acquireUnitOfWork();
        unitofWork.readAllObjects(Address.class);
        UnitOfWork nestedUnitofWork = unitofWork.acquireUnitOfWork();
        nestedUnitofWork.readAllObjects(Address.class);
        nestedUnitofWork.commit();
        unitofWork.commit();
    }
}
}
