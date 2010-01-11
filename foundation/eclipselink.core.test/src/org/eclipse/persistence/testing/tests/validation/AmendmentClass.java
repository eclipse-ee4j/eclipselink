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
