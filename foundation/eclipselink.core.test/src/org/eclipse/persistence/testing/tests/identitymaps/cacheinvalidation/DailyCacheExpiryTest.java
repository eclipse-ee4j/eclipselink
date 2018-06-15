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
package org.eclipse.persistence.testing.tests.identitymaps.cacheinvalidation;

import java.util.*;
import org.eclipse.persistence.descriptors.invalidation.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.framework.*;

/**
 *  Test daily expiry time with a variable difference between the current
 *  time and the expiry time.
 */
public class

DailyCacheExpiryTest extends CacheExpiryTest {

    protected long millisUntilExpiry = 0;
    protected long readTimeAdjustment = 0;
    protected Employee employee = null;
    protected boolean shouldExpire = false;
    protected String initialFirstName = null;

    public DailyCacheExpiryTest(long millisUntilExpiry, long readTimeAdjustment, boolean shouldExpire) {
        setDescription("Test a daily cache expiry policy with various different expiry times.");
        this.millisUntilExpiry = millisUntilExpiry;
        this.shouldExpire = shouldExpire;
        this.readTimeAdjustment = readTimeAdjustment;
    }

    public void setup() {
        super.setup();
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(new Date(System.currentTimeMillis() + millisUntilExpiry));
        DailyCacheInvalidationPolicy policy =
            new DailyCacheInvalidationPolicy(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                                             calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
        getSession().getDescriptor(Employee.class).setCacheInvalidationPolicy(policy);
    }

    public void test() {
        employee = (Employee)getSession().readObject(Employee.class);
        initialFirstName = employee.getFirstName();
        CacheKey key = ((AbstractSession)getSession()).getIdentityMapAccessorInstance().getCacheKeyForObject(employee);
        key.setReadTime(key.getReadTime() + readTimeAdjustment);
        Employee empFromCache = (Employee)key.getObject();
        empFromCache.setFirstName(empFromCache.getFirstName() + "-mutated");
        employee = (Employee)getSession().readObject(Employee.class);
    }

    public void verify() {
        if (shouldExpire && (!employee.getFirstName().equals(initialFirstName)) ||
            (!shouldExpire && (employee.getFirstName().equals(initialFirstName)))) {
            throw new TestErrorException("Daily Cache Expiry failed with expiry time different from " + "current time by " +
                                         millisUntilExpiry + " millis.");
        }
    }

}
