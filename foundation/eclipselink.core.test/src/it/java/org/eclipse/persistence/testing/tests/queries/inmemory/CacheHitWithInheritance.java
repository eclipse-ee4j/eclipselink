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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;

public class CacheHitWithInheritance extends TestCase {
    public Project projectFromCache;

    public CacheHitWithInheritance() {
        setDescription("This tests whether the right instance is returned from a cache hit when using with Inheritance.");

    }

    @Override
    public void reset() {
    }

    @Override
    public void setup() {
        //clear the cache.
        getSession().getIdentityMapAccessor().initializeAllIdentityMaps();

    }

    @Override
    public void test() {
        ExpressionBuilder bldr = new ExpressionBuilder();
        Expression exp = bldr.get("name").equal("Problem Reporting System");
        LargeProject lp = (LargeProject)getSession().readObject(LargeProject.class, exp);
        Expression exp2 = bldr.get("id").equal(lp.getId());
        projectFromCache = (Project)getSession().readObject(SmallProject.class, exp2);

    }

    @Override
    public void verify() {
        if (projectFromCache != null) {
            if (projectFromCache.getClass().getName().equals("SmallProject")) {
                throw new TestErrorException("Small and Large projects with same ID!");
            } else {
                throw new TestErrorException("Instance of wrong class is returned by cache hit.");
            }
        }
    }
}
