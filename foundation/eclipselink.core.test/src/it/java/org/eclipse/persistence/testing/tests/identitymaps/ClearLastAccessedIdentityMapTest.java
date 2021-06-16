/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.identitymaps;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.internal.identitymaps.*;

/**
 * Bug 3745484
 * Ensure the caching of lastIdentityMapAccessed does not result in the incorrect
 * identity map being returned by getIdentityMap() immediately after it has been initialized
 */
public class ClearLastAccessedIdentityMapTest extends TestCase {
    protected boolean mapNotRefreshed = false;

    public ClearLastAccessedIdentityMapTest() {
        setDescription("Ensure that after initializeIdentityMap is called, the old " + "identity map is not returned because of the caching of lastIdentityMapAccessed.");
    }

    public void setup() {
        mapNotRefreshed = false;
    }

    public void test() {
        IdentityMap originalMap = getAbstractSession().getIdentityMapAccessorInstance().getIdentityMap(Employee.class);
        getAbstractSession().getIdentityMapAccessorInstance().initializeIdentityMap(Employee.class);
        IdentityMap newMap = getAbstractSession().getIdentityMapAccessorInstance().getIdentityMap(Employee.class);
        if (originalMap == newMap) {
            mapNotRefreshed = true;
        }
    }

    public void verify() {
        if (mapNotRefreshed) {
            throw new TestErrorException("InitializeIdentityMap(Class) did not properly refresh the identity map cache.");
        }
    }
}
