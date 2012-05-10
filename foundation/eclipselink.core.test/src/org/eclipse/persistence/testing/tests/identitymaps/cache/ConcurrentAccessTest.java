/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

public class ConcurrentAccessTest extends AutoVerifyTestCase {
    CacheIdentityMap cache;

    public ConcurrentAccessTest() {
    }

    public void test() {
        this.cache = new CacheIdentityMap(10);

        org.eclipse.persistence.testing.models.employee.domain.Employee employee = new org.eclipse.persistence.testing.models.employee.domain.Employee();
        java.math.BigDecimal id = new java.math.BigDecimal(7777);
        java.util.Vector primaryKeys = new java.util.Vector();

        employee.setId(id);
        employee.setFirstName("Joe");
        employee.setLastName("Blow");
        primaryKeys.addElement(id);

        ConcurrentAccessTest.Updater updater1 = new ConcurrentAccessTest.Updater(employee, primaryKeys, this.cache);
        Thread thread1 = new Thread(updater1);

        employee = new org.eclipse.persistence.testing.models.employee.domain.Employee();
        id = new java.math.BigDecimal(6666);
        primaryKeys = new java.util.Vector();

        employee.setId(id);
        employee.setFirstName("Andy");
        employee.setLastName("Blow");
        primaryKeys.addElement(id);

        ConcurrentAccessTest.Updater updater2 = new ConcurrentAccessTest.Updater(employee, primaryKeys, this.cache);
        Thread thread2 = new Thread(updater2);

        employee = new org.eclipse.persistence.testing.models.employee.domain.Employee();
        id = new java.math.BigDecimal(5555);
        primaryKeys = new java.util.Vector();

        employee.setId(id);
        employee.setFirstName("Darlene");
        employee.setLastName("Blow");
        primaryKeys.addElement(id);

        ConcurrentAccessTest.Updater updater3 = new ConcurrentAccessTest.Updater(employee, primaryKeys, this.cache);
        Thread thread3 = new Thread(updater3);

        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException ex) {
        }
        if (updater1.hadError() || updater2.hadError() || updater3.hadError()) {
            throw new TestErrorException("Null Pointer thrown within Cache Access");
        }
    }

    private class Updater implements Runnable {
        protected Object object;
        protected java.util.Vector primaryKeys;
        protected CacheIdentityMap cache;
        protected boolean experienceError = false;

        public Updater(Object object, java.util.Vector primaryKeys, CacheIdentityMap cache) {
            this.object = object;
            this.primaryKeys = primaryKeys;
            this.cache = cache;
        }

        public void run() {
            try {
                for (int count = 0; count < 10000; ++count) {
                    cache.put(primaryKeys, object, null, 0);
                    cache.remove(primaryKeys, null);
                }
            } catch (NullPointerException ex) {
                this.experienceError = true;
            }
        }

        public boolean hadError() {
            return experienceError;
        }
    }
}
