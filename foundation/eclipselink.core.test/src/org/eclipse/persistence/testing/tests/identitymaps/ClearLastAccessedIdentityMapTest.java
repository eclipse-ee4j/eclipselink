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
