/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.internal.xr;

// Javase imports

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * <p><b>INTERNAL:</b> JPQLQueryHandler sets the JPQL string in the given {@link DatabaseQuery}
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class JPQLQueryHandler extends QueryHandler {

    protected String jpqlString;

    public String getJpqlString() {
        return jpqlString;
    }
    public void setJpqlString(String sqlString) {
        this.jpqlString = sqlString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeCall(XRServiceAdapter xrService, QueryOperation queryOperation,
        DatabaseQuery databaseQuery) {

        databaseQuery.setEJBQLString(jpqlString);
    }
}
