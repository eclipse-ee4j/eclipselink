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
package org.eclipse.persistence.testing.tests.customsqlstoredprocedures;

import org.eclipse.persistence.testing.framework.*;

/**
 * Test selecting using an object's primary key to ensure that it does not go to the databaase.
 */
public class CacheHitTest extends org.eclipse.persistence.testing.tests.queries.inmemory.CacheHitTest {
    public CacheHitTest() {
        super();
    }

    public CacheHitTest(Object originalObject) {
        super(originalObject);
    }

    protected void setup() {
        if (!((getSession().getLogin().getPlatform().isOracle()) || (getSession().getLogin().getPlatform().isSQLServer()) || (getSession().getLogin().getPlatform().isSybase()) || (getSession().getLogin().getPlatform().isSQLAnywhere()) || (getSession().getLogin().getPlatform().isMySQL()) || (getSession().getLogin().getPlatform().isSymfoware()) || (getSession().getLogin().getPlatform().isPervasive()))) {
            throw new TestWarningException("This test is only valid for Database Systems supporting StoredProcedures");
        }
        super.setup();
    }
}
