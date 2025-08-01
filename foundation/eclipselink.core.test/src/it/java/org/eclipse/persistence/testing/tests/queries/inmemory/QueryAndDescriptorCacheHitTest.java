/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;

//Super class for 6 combinations of query and descriptor shouldMaintainCache property.
//Query's shouldMaintainCache could be undefined, true or false.  Descriptor's
//shouldMaintainCache (!shouldDisableCacheHits) could be true or false
public class QueryAndDescriptorCacheHitTest extends CacheHitTest {
    protected Employee employee;
    protected ClassDescriptor descriptor;
    protected boolean orgDisableCacheHits;
    protected String firstName;

    public QueryAndDescriptorCacheHitTest() {
        originalObject = PopulationManager.getDefaultManager().getObject(Employee.class, "0001");
    }

    @Override
    protected void setup() {
        super.setup();
        descriptor = getSession().getClassDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        orgDisableCacheHits = descriptor.shouldDisableCacheHits();
        Employee emp = (Employee)getSession().getIdentityMapAccessor().getFromIdentityMap(objectToRead);
        emp.setFirstName(emp.getFirstName() + "-changed");
        firstName = emp.getFirstName();
    }

    @Override
    public void reset() {
        super.reset();
        descriptor.setShouldDisableCacheHits(orgDisableCacheHits);
    }

    @Override
    protected Object readObject() {
        ExpressionBuilder builder = new ExpressionBuilder();
        Expression exp = (builder.get("id").equal(((Employee)objectToRead).getId()));
        ReadObjectQuery query = new ReadObjectQuery(Employee.class);
        query.setSelectionCriteria(exp);
        return readObject(query);
    }

    protected Object readObject(ReadObjectQuery query) {
        return getSession().executeQuery(query);
    }
}
