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
package org.eclipse.persistence.testing.tests.stress;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;

/**
 * Test insert many times.
 */

public class StressUnitOfWorkTest extends AutoVerifyTestCase {
    public int stressLevel;
public StressUnitOfWorkTest(int stressLevel)
{
    this.stressLevel = stressLevel;
}
@Override
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
