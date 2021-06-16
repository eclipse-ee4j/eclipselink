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
package org.eclipse.persistence.testing.tests.identitymaps.cache;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class InsertOverflowTest extends AutoVerifyTestCase {
    CacheIdentityMap cache;
    int originalMaxSize;

    public InsertOverflowTest(CacheIdentityMap cache) {
        this.cache = cache;
        this.originalMaxSize = cache.getMaxSize();
    }

    public void test() {
        for (int index = 0; index < (originalMaxSize * 10); index++) {
            Employee employee = new Employee();
            java.math.BigDecimal id = new java.math.BigDecimal((double)index * 101);
            java.util.Vector primaryKeys = new java.util.Vector();
            employee.setId(id);
            employee.setFirstName("Mr. " + id);
            primaryKeys.addElement(id);
            cache.put(primaryKeys, employee, null, 0);
        }
    }

    public void verify() {
        // Check to see if maximum size was maintained.
        if (cache.getSize() != originalMaxSize) {
            throw new TestErrorException("Cache identity map " + cache + " contains " + cache.getSize() + " objects.  " + "The specified maximum size for this cache was " + originalMaxSize + ".");
        }

        // See if cache actually contains last proper elements
        for (int index = ((originalMaxSize * 10) - originalMaxSize);
                 index < (originalMaxSize * 10); index++) {
            java.math.BigDecimal id = new java.math.BigDecimal((double)index * 101);
            java.util.Vector primaryKeys = new java.util.Vector();
            primaryKeys.addElement(id);
            if (!cache.containsKey(primaryKeys)) {
                throw new TestErrorException("Cache should contain value with keys " + primaryKeys + " but does not.");
            }
        }
    }
}
