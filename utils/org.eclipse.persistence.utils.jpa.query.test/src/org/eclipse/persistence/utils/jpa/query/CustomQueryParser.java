/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query;

import java.util.Map;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;

/**
 * Custom {@link JPQLQueryParser} implementation used by the {@link QueryBuilderTests}.
 *
 * @version 2.3
 * @since 2.3
 * @author John Bracken
 */
public final class CustomQueryParser /*implements JPQLQueryParser*/ {

    /**
     * Default, public zero-arg constructor that is required.
     */
    public CustomQueryParser() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseQuery buildQuery(String jpqlQuery, AbstractSession session) {
        return new ReadAllQuery();
    }

    /**
     * {@inheritDoc}
     */
    public Expression buildSelectionCriteria(String abstractSchemaName,
                                             String selectionCriteria,
                                             AbstractSession session,
                                             Map<String, Class<?>> arguments) {

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void populateQuery(String jpqlQuery, DatabaseQuery query, AbstractSession session) {
        // No-op
    }
}