/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.testing.framework.*;

/**
 * Test insert many times.
 */

public class StressInsertTest extends AutoVerifyTestCase {
    public int stressLevel;
public StressInsertTest(int stressLevel)
{
    this.stressLevel = stressLevel;
}
public void test( )
{
    long seq = 900000000;
    seq = seq * 1000;
    for (int i = 0; i < stressLevel; i++) {
        Address address = new Address();
        getDatabaseSession().insertObject(address);
        if (address.getId() < seq) {
            throw new TestErrorException("Sequence id was truncated.");
        }
    }
}
}
