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
package org.eclipse.persistence.testing.models.jpa.composite.advanced.member_2;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.mappings.querykeys.OneToManyQueryKey;
import org.eclipse.persistence.mappings.querykeys.OneToOneQueryKey;
import org.eclipse.persistence.testing.models.jpa.composite.advanced.member_3.PhoneNumber;

public class EmployeeCustomizer implements DescriptorCustomizer {
    public EmployeeCustomizer() {}

    public void customize(ClassDescriptor descriptor) {
        descriptor.setShouldDisableCacheHits(false);

        descriptor.addDirectQueryKey("startTime", "START_TIME");

        OneToOneQueryKey queryKey = new OneToOneQueryKey();
        queryKey.setName("boss");
        queryKey.setReferenceClass(Employee.class);
        ExpressionBuilder builder = new ExpressionBuilder();
        queryKey.setJoinCriteria(builder.getField("MANAGER_EMP_ID").equal(builder.getParameter("EMP_ID")));
        descriptor.addQueryKey(queryKey);

        OneToManyQueryKey otmQueryKey = new OneToManyQueryKey();
        otmQueryKey.setName("phoneQK");
        otmQueryKey.setReferenceClass(PhoneNumber.class);
        builder = new ExpressionBuilder();
        otmQueryKey.setJoinCriteria(builder.getField("OWNER_ID").equal(builder.getParameter("EMP_ID")));
        descriptor.addQueryKey(otmQueryKey);
    }
}
