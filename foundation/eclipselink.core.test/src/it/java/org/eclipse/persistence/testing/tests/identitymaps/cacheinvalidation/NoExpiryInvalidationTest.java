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
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.descriptors.invalidation.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.models.employee.domain.*;

public class NoExpiryInvalidationTest extends CacheExpiryTest {

    protected String firstName = null;
    protected Employee employee = null;

    public NoExpiryInvalidationTest() {
        setDescription("Test the invalidation API with a NoExpiryCacheExpiryPolicy.");
    }

    public void setup() {
        super.setup();
        getSession().getDescriptor(Employee.class).setCacheInvalidationPolicy(new NoExpiryCacheInvalidationPolicy());
    }

    public void test() {
        employee = (Employee)getSession().readObject(Employee.class);
        Employee empFromCache =
            (Employee)((AbstractSession)getSession()).getIdentityMapAccessorInstance().getCacheKeyForObject(employee).getObject();
        firstName = empFromCache.getFirstName();
        empFromCache.setFirstName(empFromCache.getFirstName() + "-mutated");
        getAbstractSession().getIdentityMapAccessor().invalidateObject(employee);
        employee = (Employee)getSession().readObject(employee);
    }

    public void verify() {
        if (!employee.getFirstName().equals(firstName)) {
            throw new TestErrorException("Invalidation did not work with NoExpiryCacheExpiryPolicy.");
        }
    }
}
