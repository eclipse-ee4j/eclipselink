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
        return getAbstractSession().getIdentityMapAccessorInstance().getWriteLockValue(getSession().keyFromObject(domainObject), domainObject.getClass());
    }

    private void setWriteLockValue(Object domainObject, Object writeLockValue) {
        getAbstractSession().getIdentityMapAccessorInstance().getIdentityMapManager().setWriteLockValue(getSession().keyFromObject(domainObject), domainObject.getClass(), writeLockValue);
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
