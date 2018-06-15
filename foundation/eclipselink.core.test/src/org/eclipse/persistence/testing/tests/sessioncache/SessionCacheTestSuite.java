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
package org.eclipse.persistence.testing.tests.sessioncache;

import org.eclipse.persistence.testing.framework.*;

public class SessionCacheTestSuite extends TestSuite {
    public SessionCacheTestSuite() {
        setDescription("This suite tests that we only merge into the session cache when necessary.");
    }

    public void addTests() {
        addTest(new ReadObjectAlreadyInSessionCacheTest());
        addTest(new ReadObjectNotInSessionCacheTest());
        addTest(new ReadReferencedObjectNotInSessionCacheTest());
        addTest(new WriteNewObjectTest());
        addTest(new WriteNewPessimisticLockedObjectTest());
    }
}
