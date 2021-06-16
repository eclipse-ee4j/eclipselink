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

/**
 * Register a single object in the identity map with a write lock value. <p>
 * The identity map method setWriteLockValue(Vector, Object) sets the lock
 * value of the object whose primary key is in Vector. This class tests this method.<p>
 */
public class SetWriteLockInIdentityMapTest extends RegisterInIdentityMapTest {
    protected Object originalWriteLockValue;
    protected Object retrievedWriteLockValue;

    public SetWriteLockInIdentityMapTest(Class mapClass) {
        super(mapClass);
        originalWriteLockValue = new java.math.BigDecimal("9999999999999999999999999");
    }

    private Object getWriteLockValue(Object domainObject) {
        return getAbstractSession().getIdentityMapAccessorInstance().getWriteLockValue(getSession().getId(domainObject), domainObject.getClass());
    }

    private void setWriteLockValue(Object domainObject, Object writeLockValue) {
        getAbstractSession().getIdentityMapAccessorInstance().getIdentityMapManager().setWriteLockValue(getSession().getId(domainObject), domainObject.getClass(), writeLockValue);
    }

    public void test() {
        super.test();
        setWriteLockValue(employees.lastElement(), originalWriteLockValue);
        retrievedWriteLockValue = getWriteLockValue(employees.lastElement());
    }

    public void verify() {
        if (!originalWriteLockValue.equals(retrievedWriteLockValue) && !isNoIdentityMap()) {
            throw new TestErrorException("Write lock original: " + originalWriteLockValue + " did not match cached: " + retrievedWriteLockValue);
        }
    }
}
