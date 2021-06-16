/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     09/28/2015 - Will Dazey
//       - 478331 : Added support for defining local or server as the default locale for obtaining timestamps
package org.eclipse.persistence.jpa.test.locking.model;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.TimestampLockingPolicy;
import org.eclipse.persistence.internal.descriptors.OptimisticLockingPolicy;
import org.junit.Assert;

public class ClassDescriptorCustomizer implements DescriptorCustomizer {
    public static int USETIME;

    @Override
    public void customize(ClassDescriptor descriptor) throws Exception {
        OptimisticLockingPolicy policy = descriptor.getOptimisticLockingPolicy();
        if (policy instanceof TimestampLockingPolicy) {
            if (USETIME != 0) {
                ((TimestampLockingPolicy) policy).setUsesServerTime((USETIME == TimestampLockingPolicy.SERVER_TIME) ? true : false);
            }
        } else {
            Assert.fail("Check that ClassDescriptor version locking using java.sql.Timestamp");
        }
    }
}
