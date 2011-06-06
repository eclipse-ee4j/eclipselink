/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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

import java.util.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.identitymaps.*;

/**
 * This test was designed to detect a bug in which if a cache identity map was of size 2 then deleting
 * from this identity map would result in a NullPointerException
 */
public class CustomDeleteTest extends org.eclipse.persistence.testing.framework.AutoVerifyTestCase {
    public CacheIdentityMap cache;

    public CustomDeleteTest(CacheIdentityMap cache) {
        this.cache = cache;
    }

    public void setup() {
        org.eclipse.persistence.testing.models.employee.domain.Employee employee = new org.eclipse.persistence.testing.models.employee.domain.Employee();
        java.math.BigDecimal id = new java.math.BigDecimal(7777);
        java.util.Vector primaryKeys = new java.util.Vector();
        employee.setId(id);
        employee.setFirstName("Joe");
        employee.setLastName("Blow");
        primaryKeys.addElement(id);
        cache.put(primaryKeys, employee, null, 0);
        employee = new org.eclipse.persistence.testing.models.employee.domain.Employee();
        id = new java.math.BigDecimal(5678);
        primaryKeys = new Vector();
        employee.setId(id);
        employee.setFirstName("Joeline");
        employee.setLastName("Carson");
        primaryKeys.addElement(id);
        cache.put(primaryKeys, employee, null, 0);

        id = new java.math.BigDecimal(5978);
        primaryKeys = new Vector();
        employee.setId(id);
        employee.setFirstName("Joel");
        employee.setLastName("Cars");
        primaryKeys.addElement(id);
        cache.put(primaryKeys, employee, null, 0);

    }

    public void test() {
        try {
            Vector primaryKeys = new Vector();
            primaryKeys.addElement(new java.math.BigDecimal(5678));
            cache.remove(primaryKeys, null);
        } catch (NullPointerException e) {
            throw new TestErrorException("Error deleteing from cache when size is 2");
        }
    }

    public void verify() {
    }
}
