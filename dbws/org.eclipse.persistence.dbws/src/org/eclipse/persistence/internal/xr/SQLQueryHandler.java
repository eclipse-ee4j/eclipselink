/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.internal.xr;

// Javase imports

// Java extension imports

// EclipseLink imports
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.SQLCall;

/**
 * <p><b>INTERNAL:</b> JPQLQueryHandler sets the JPQL string in the given {@link DatabaseQuery}
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class SQLQueryHandler extends QueryHandler {

    protected String sqlString;

    public String getSqlString() {
        return sqlString;
    }

    public void setSqlString(String sqlString) {
        this.sqlString = sqlString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(XRServiceAdapter xrService, QueryOperation queryOperation) {
        super.initialize(xrService, queryOperation);
        getDatabaseQuery().setIsUserDefined(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeCall(XRServiceAdapter xrService, QueryOperation queryOperation,
        DatabaseQuery databaseQuery) {
        databaseQuery.setCall(new SQLCall(getSqlString()));
    }
}
