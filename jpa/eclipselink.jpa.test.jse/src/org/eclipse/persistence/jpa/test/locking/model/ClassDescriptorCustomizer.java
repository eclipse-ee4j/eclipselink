/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     09/28/2015 - Will Dazey
 *       - 478331 : Added support for defining local or server as the default locale for obtaining timestamps
 ******************************************************************************/
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