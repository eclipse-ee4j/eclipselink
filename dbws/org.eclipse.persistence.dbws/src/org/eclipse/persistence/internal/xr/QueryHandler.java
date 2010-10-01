/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
import javax.xml.namespace.QName;

// EclipseLink imports
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ValueReadQuery;

/**
 * <p><b>INTERNAL:</b> <code>QueryHandler</code> sets out the basic rules for how Operations can
 * use different types of queries (DataRead, ValueRead, etc.)
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public abstract class QueryHandler {

    protected DatabaseQuery databaseQuery;

    public DatabaseQuery getDatabaseQuery() {
        return databaseQuery;
    }

    public void setDatabaseQuery(DatabaseQuery databaseQuery) {
        this.databaseQuery = databaseQuery;
    }

    @SuppressWarnings("unused")
    public void validate(XRServiceAdapter xrService, QueryOperation queryOperation) {
    }

    public void initialize(XRServiceAdapter xrService, QueryOperation queryOperation) {
        initializeDatabaseQuery(xrService, queryOperation);
        initializeCall(xrService, queryOperation, getDatabaseQuery());
        initializeArguments(xrService, queryOperation, getDatabaseQuery());
    }

    /**
     * <p><b>INTERNAL</b>: Initialize this <code>QueryHandler</code>'s <code>DatabaseQuery</code>
     * @param dbwsService      the given <code>DBWSService</code>
     * @param queryOperation the given <code>QueryOperation</code>
     */
    public void initializeDatabaseQuery(XRServiceAdapter xrService, QueryOperation queryOperation) {

        DatabaseQuery databaseQueryToInitialize;

        if (queryOperation.hasResponse()) {
            QName type = queryOperation.getResult().getType();
            if (queryOperation.isCollection()) {
                if (queryOperation.isSimpleXMLFormat() ||
                    (xrService.descriptorsByQName.containsKey(type) &&
                     xrService.getORSession().getClassDescriptorForAlias(
                      xrService.descriptorsByQName.get(type).getAlias()).isAggregateDescriptor() &&
                     !xrService.getORSession().getClassDescriptorForAlias(
                      xrService.descriptorsByQName.get(type).getAlias()).isObjectRelationalDataTypeDescriptor()
                    )) {
                    // data-read query
                    databaseQueryToInitialize = new DataReadQuery();
                }
                else {
                    if (!xrService.descriptorsByQName.containsKey(type)) {
                        // data-read query
                        databaseQueryToInitialize = new DataReadQuery();
                    }
                    else {
                        // read-all query for the class mapped to the type
                        databaseQueryToInitialize =
                            new ReadAllQuery(xrService.getTypeClass(type));
                    }
                }
            }
            else {
                if (queryOperation.isSimpleXMLFormat() ||
                    (xrService.descriptorsByQName.containsKey(type) &&
                     xrService.getORSession().getClassDescriptorForAlias(
                      xrService.descriptorsByQName.get(type).getAlias()).isAggregateDescriptor() &&
                     !xrService.getORSession().getClassDescriptorForAlias(
                      xrService.descriptorsByQName.get(type).getAlias()).isObjectRelationalDataTypeDescriptor()
                    )) {
                    // data-read query
                    databaseQueryToInitialize = new DataReadQuery();
                }
                else if (!xrService.descriptorsByQName.containsKey(type)) {
                    // value read query
                    databaseQueryToInitialize = new ValueReadQuery();
                }
                else {
                    // read object query for the class mapped to the type
                    databaseQueryToInitialize = new ReadObjectQuery(xrService.getTypeClass(type));
                }
            }
        }
        else {
            databaseQueryToInitialize = new DataModifyQuery();
        }
        databaseQueryToInitialize.bindAllParameters();
        setDatabaseQuery(databaseQueryToInitialize);
    }

    /**
     * <p><b>INTERNAL</b>: Initialize this <code>QueryHandler</code>'s <code>DatabaseQuery</code>'s
     * {@link Call}. Typically no work is required, but for some <code>QueryHandlers</code>
     * (<code>JPQLQueryHandler</code>, <code>StoredProcedureQueryHandler</code>, etc.) special
     * handling may be required.
     * @param dbwsService      the given <code>DBWSService</code>
     * @param queryOperation the given <code>QueryOperation</code>
     * @param databaseQuery the given <code>DatabaseQuery</code>
     */
    @SuppressWarnings("unused")
    public void initializeCall(XRServiceAdapter xrService, QueryOperation queryOperation,
        DatabaseQuery databaseQuery) {
    }

    /**
     * <p><b>INTERNAL</b>: Initialize this <code>QueryHandler</code>'s <code>DatabaseQuery</code>'s
     * arguments from the {@link Operation}'s {@link Parameter Parameters}
     * @param dbwsService      the given <code>DBWSService</code>
     * @param queryOperation the given <code>QueryOperation</code>
     * @param databaseQuery the given <code>DatabaseQuery</code>
     */
    @SuppressWarnings("unused")
    public void initializeArguments(XRServiceAdapter xrService, QueryOperation queryOperation,
        DatabaseQuery databaseQuery) {
        for (int i = 0; i < queryOperation.getParameters().size(); i++) {
            Object o = queryOperation.getParameters().get(i);
            if (o instanceof Parameter) {
                Parameter p = (Parameter)o;
                String name = p.getName();
                if (name != null && name.length() > 0 ) {
                    databaseQuery.addArgument(name);
                    continue;
                }
            }
            String s = Integer.toString(i + 1);
            databaseQuery.addArgument(s);
        }
    }
}
