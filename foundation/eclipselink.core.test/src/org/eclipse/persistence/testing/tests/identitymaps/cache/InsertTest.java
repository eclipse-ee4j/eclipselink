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
package org.eclipse.persistence.testing.tests.identitymaps.cache;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

public class InsertTest extends AutoVerifyTestCase {
    CacheIdentityMap cache;
    org.eclipse.persistence.testing.models.employee.domain.Employee employee;

    public InsertTest(CacheIdentityMap cache) {
        this.cache = cache;
    }

    public void test() {
        Employee employee = new Employee();
        java.math.BigDecimal id = new java.math.BigDecimal(7777);
        java.util.Vector primaryKeys = new java.util.Vector();

        employee.setId(id);
        employee.setFirstName("Joe");
        employee.setLastName("Blow");
        primaryKeys.addElement(id);
        cache.put(primaryKeys, employee, null, 0);

        this.employee = employee;
    }

    public void verify() {
        // Check to see if element just inserted is last element.
        java.math.BigDecimal id = new java.math.BigDecimal(7777);
        java.util.Vector primaryKeys = new java.util.Vector();
        primaryKeys.addElement(id);

        if (!cache.containsKey(primaryKeys)) {
            throw new TestErrorException("Cache identity map " + cache + " did not insert " + employee + " " + primaryKeys + " into the cache.");
        }
    }
}
