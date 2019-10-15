/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.tools;

import org.eclipse.persistence.jpa.jpql.tools.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.tools.spi.IQuery;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * This unit-test tests {@link AbstractJPQLQueryHelper} when the queries are on entities managed by
 * an ORM configuration.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
@RunWith(JPQLTestRunner.class)
public final class ORMEntityJPQLQueryHelperTest extends AbstractJPQLQueryHelperTest {

    private IEntity entity(String entityName) throws Exception {
        IEntity entity = getORMConfiguration("orm1.xml").getEntityNamed(entityName);
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
}
