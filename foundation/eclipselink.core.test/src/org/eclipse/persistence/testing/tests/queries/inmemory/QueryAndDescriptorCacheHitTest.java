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
package org.eclipse.persistence.testing.tests.queries.inmemory;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.tools.schemaframework.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.queries.ReadObjectQuery;

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

    protected void setup() {
        super.setup();
        descriptor = getSession().getClassDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        orgDisableCacheHits = descriptor.shouldDisableCacheHits();
        Employee emp = (Employee)getSession().getIdentityMapAccessor().getFromIdentityMap(objectToRead);
        emp.setFirstName(emp.getFirstName() + "-changed");
        firstName = emp.getFirstName();
    }

    public void reset() {
        super.reset();
        descriptor.setShouldDisableCacheHits(orgDisableCacheHits);
    }

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
