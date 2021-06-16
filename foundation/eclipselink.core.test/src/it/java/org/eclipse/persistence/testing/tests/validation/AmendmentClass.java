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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;


public class AmendmentClass {
    public AmendmentClass() {
    }

    public static void modifyPhoneDescriptor(ClassDescriptor descriptor, Address address) {
        ExpressionBuilder builder = new ExpressionBuilder();
        ReadAllQuery query = new ReadAllQuery(org.eclipse.persistence.testing.models.employee.domain.PhoneNumber.class, builder);

        Expression exp = builder.get("id").equal(builder.getParameter("ID"));
        query.setSelectionCriteria(exp.and(builder.get("areaCode").equal("613")));

        query.addArgument("ID");

        descriptor.getQueryManager().addQuery("localNumbers", query);
    }

    public void modifyPhoneDescriptorAgain(ClassDescriptor descriptor) {
        ExpressionBuilder builder = new ExpressionBuilder();
        ReadAllQuery query = new ReadAllQuery(org.eclipse.persistence.testing.models.employee.domain.PhoneNumber.class, builder);

        Expression exp = builder.get("id").equal(builder.getParameter("ID"));
        query.setSelectionCriteria(exp.and(builder.get("areaCode").equal("514")));

        query.addArgument("ID");

        descriptor.getQueryManager().addQuery("montrealNumbers", query);
    }

}
