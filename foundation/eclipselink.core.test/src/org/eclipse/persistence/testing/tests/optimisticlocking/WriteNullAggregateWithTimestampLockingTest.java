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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.models.optimisticlocking.*;
import org.eclipse.persistence.testing.framework.TransactionalTestCase;

public class WriteNullAggregateWithTimestampLockingTest extends TransactionalTestCase {

    public WriteNullAggregateWithTimestampLockingTest() {
        super();
        setDescription("Test writing an object with a null aggregate and a TimestampLockingPolicy");
    }

    @Override
    public void test() {
        UnitOfWork uow = getSession().acquireUnitOfWork();
        TimestampInAggregateObject original = new TimestampInAggregateObject();
        original.version = null; // set to null
        TimestampInAggregateObject clone = (TimestampInAggregateObject)uow.registerObject(original);
        clone.value = "Timestamp in null aggregate";
        uow.commit();
    }

}
