/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Date;
import org.eclipse.persistence.jpa.jpql.tools.AbstractJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.tools.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.tools.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.tools.spi.IType;
import org.eclipse.persistence.jpa.tests.jpql.UniqueSignature;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@UniqueSignature
@SuppressWarnings("nls")
public final class DefaultJPQLQueryHelperTest2_1 extends AbstractJPQLQueryHelperTest {

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
    public void test_ResultType_Treat_1() throws Exception {

        // SELECT TREAT(TREAT(p.project LargeProject).parent AS LargeProject).endDate
        // FROM Product p
        IQuery namedQuery = namedQuery("Product", "product.treat");

        AbstractJPQLQueryHelper helper = buildQueryHelper(namedQuery);
        IType type = helper.getResultType();

        assertNotNull(
            "The type of TREAT(TREAT(p.project LargeProject).parent AS LargeProject).endDate should have been found",
            type
        );

        assertEquals(
            "The wrong type for TREAT(TREAT(p.project LargeProject).parent AS LargeProject).endDate was retrieved",
            getType(namedQuery, Date.class),
            type
        );
    }
}
