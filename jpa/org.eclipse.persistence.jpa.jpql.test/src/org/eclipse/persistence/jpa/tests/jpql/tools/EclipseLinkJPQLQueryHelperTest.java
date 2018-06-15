/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.tools;

import org.eclipse.persistence.jpa.jpql.tools.AbstractJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.tools.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.tools.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class EclipseLinkJPQLQueryHelperTest extends AbstractJPQLQueryHelperTest {

    protected IEntity entity(String entityName) throws Exception {
        IEntity entity = getPersistenceUnit().getEntityNamed(entityName);
        assertNotNull("The entity " + entityName + " could not be found", entity);
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IQuery namedQuery(String entityName, String queryName) throws Exception {
        IEntity entity = entity(entityName);
        IQuery namedQuery = entity.getNamedQuery(queryName);
        assertNotNull("The named query " + queryName + " could not be found on " + entityName, namedQuery);
        return namedQuery;
    }

    @Test
    public void test_ResultType_Func_1() throws Exception {

        // SELECT FUNC('toString', e.name) FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.func1");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of FUNC('toString', e.name) should have been found",
            type
        );

        assertEquals(
            "The wrong type for FUNC('toString', e.name) was retrieved",
            getType(namedQuery, Object.class),
            type
        );
    }

    @Test
    public void test_ResultType_Func_2() throws Exception {

        // SELECT FUNC('age', e.empId, e.salary) FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.func2");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of FUNC('age', e.empId, e.salary) should have been found",
            type
        );

        assertEquals(
            "The wrong type for FUNC('age', e.empId, e.salary) was retrieved",
            getType(namedQuery, Object.class),
            type
        );
    }

    @Test
    public void test_ResultType_Func_3() throws Exception {

        // SELECT FUNC('age', e.empId, e.name) FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.func3");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of FUNC('toString', e.name) should have been found",
            type
        );

        assertEquals(
            "The wrong type for FUNC('toString', e.name) was retrieved",
            getType(namedQuery, Object.class),
            type
        );
    }

    @Test
    public void test_ResultType_Func_4() throws Exception {

        // SELECT FUNC('age', e.empId, ?name) FROM Employee e
        IQuery namedQuery = namedQuery("Employee", "employee.func4");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of FUNC('age', e.empId, ?name) should have been found",
            type
        );

        assertEquals(
            "The wrong type for FUNC('age', e.empId, ?name) was retrieved",
            getType(namedQuery, Object.class),
            type
        );
    }
}
