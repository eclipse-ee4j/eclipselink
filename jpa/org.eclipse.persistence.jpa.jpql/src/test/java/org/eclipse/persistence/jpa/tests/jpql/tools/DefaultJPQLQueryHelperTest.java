/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.tools;

import org.eclipse.persistence.jpa.jpql.tools.spi.IEntity;
import org.eclipse.persistence.jpa.jpql.tools.spi.IQuery;
import org.eclipse.persistence.jpa.tests.jpql.UniqueSignature;
import static org.junit.Assert.*;

/**
 * This unit-test tests {@link AbstractJPQLQueryHelper} when the queries are on entities managed by
 * a persistence unit.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
@UniqueSignature
@SuppressWarnings("nls")
public final class DefaultJPQLQueryHelperTest extends AbstractJPQLQueryHelperTest {

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
}
