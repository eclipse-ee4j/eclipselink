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

import java.util.*;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.descriptors.invalidation.*;
import org.eclipse.persistence.testing.framework.*;

/**
 * Allows testing of the CacheExpiry Times with various settings.
 * Can test expiring and non-expiring types
 * Can test various expiry types (TIME_TO_LIVE, DAILY, NO_EXPIRY).
 */
public class CacheExpiryPolicyTest extends CacheExpiryTest {

    protected boolean shouldObjectExpire = false;
    protected CacheInvalidationPolicy policy = null;
    protected Employee employee = null;

    public CacheExpiryPolicyTest(CacheInvalidationPolicy policy, boolean shouldObjectExpire) {
        setDescription("Test Cache Expiry with various kinds of expiry");
        this.shouldObjectExpire = shouldObjectExpire;
        this.policy = policy;
    }

    public void setup() {
        super.setup();
        getSession().getDescriptor(Employee.class).setCacheInvalidationPolicy(policy);
        employee = (Employee)getSession().readObject(Employee.class);
        Calendar c1 = new GregorianCalendar();
        if (policy instanceof DailyCacheInvalidationPolicy && shouldObjectExpire) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException exc) {
            }
            Calendar calendar = new GregorianCalendar();

            ((DailyCacheInvalidationPolicy)policy).setExpiryTime(calendar.get(Calendar.HOUR_OF_DAY),
                                                                 calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND),
                                                                 calendar.get(Calendar.MILLISECOND));
            try {
                Thread.sleep(100);
            } catch (InterruptedException exc) {
            }

            return;
        }


    }


    public void verify() {
        if ((getSession().getIdentityMapAccessor().isValid(employee)) == shouldObjectExpire) {
            throw new TestErrorException("Cache Expiry Failed.  Please ensure this system is not running " +
                                         "with a heavy load prior to filing a bug since the cache expiry tests rely to a certain degree on timing.");
        }

    }

}
