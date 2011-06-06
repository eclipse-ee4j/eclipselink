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

package org.eclipse.persistence.internal.xr;

// Javase imports
import java.util.ArrayList;
import java.util.List;

// Java extension imports
import javax.xml.namespace.QName;

// EclipseLink imports
import org.eclipse.persistence.exceptions.DBWSException;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.queries.ValueReadQuery;
import static org.eclipse.persistence.internal.xr.Util.SXF_QNAME;

/**
 * <p><b>INTERNAL:</b> StoredProcedureQueryHandler sets up the StoredProcedureCall
 * and its arguments in the given {@link DatabaseQuery}
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */

public class StoredProcedureQueryHandler extends QueryHandler {

    protected String name;
    protected List<ProcedureArgument> inArguments = new ArrayList<ProcedureArgument>();
    protected List<ProcedureOutputArgument> inOutArguments = new ArrayList<ProcedureOutputArgument>();
    protected List<ProcedureOutputArgument> outArguments = new ArrayList<ProcedureOutputArgument>();

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<ProcedureArgument> getInArguments() {
        return inArguments;
    }

    public List<ProcedureOutputArgument> getInOutArguments() {
        return inOutArguments;
    }

    public List<ProcedureOutputArgument> getOutArguments() {
        return outArguments;
    }
    public boolean isStoredFunctionQueryHandler() {
        return false;
    }

    @Override
    public void initializeDatabaseQuery(XRServiceAdapter xrService, QueryOperation queryOperation) {
        DatabaseQuery databaseQueryToInitialize;

        if (queryOperation.hasResponse()) {
            QName type = queryOperation.getResult().getType();
            if (queryOperation.isCollection()) {
                if (queryOperation.isSimpleXMLFormat()) {
                    databaseQueryToInitialize = new DataReadQuery();
                }
                else {
                    if (!xrService.descriptorsByQName.containsKey(type)) {
                        // data-read query
                        databaseQueryToInitialize = new DataReadQuery();
                    }
                    else {
                        // read-all query for the class mapped to the type
                        databaseQueryToInitialize = new ReadAllQuery(xrService.getTypeClass(type));
                    }
                }
            }
            else {
                if (getOutArguments().size() == 0 && getInOutArguments().size() == 0) {
                    if (isStoredFunctionQueryHandler()) {
                        if (!xrService.descriptorsByQName.containsKey(type)) {
                            databaseQueryToInitialize = new ValueReadQuery();
                        }
                        else {
                            // read object query for the class mapped to the type
                            databaseQueryToInitialize = new ReadObjectQuery(xrService.getTypeClass(type));
                        }
                    }
                    else {
                        // special case - no out args for SP: the return
                        // will be a single int
                        // rowcount
                        databaseQueryToInitialize = new DataModifyQuery();
                    }
                }
                else {
                    if (!xrService.descriptorsByQName.containsKey(type)) {
                    	if (type.equals(SXF_QNAME)) {
                    		databaseQueryToInitialize = new DataReadQuery();
                    	}
                    	else {
                    		databaseQueryToInitialize = new ValueReadQuery();
                    	}
                    }
                    else {
                        // read object query for the class mapped to the type
                        databaseQueryToInitialize = new ReadObjectQuery(xrService.getTypeClass(type));
                    }
                }
            }
        }
        else {
            databaseQueryToInitialize = new ValueReadQuery();
        }
        databaseQueryToInitialize.bindAllParameters();
        setDatabaseQuery(databaseQueryToInitialize);
    }

    @Override
    public void initializeCall(XRServiceAdapter xrService, QueryOperation queryOperation,
        DatabaseQuery databaseQuery) {

        StoredProcedureCall spCall = createCall();
        if (getName() != null) {
            spCall.setProcedureName(getName());
        } else {
            spCall.setProcedureName(queryOperation.getName());
        }

        QName resultType = queryOperation.getResultType();

        if ((getInOutArguments().size() + getOutArguments().size()) > 1 &&
            !queryOperation.isSimpleXMLFormat()) {
            throw DBWSException.multipleOutputArgumentsOnlySupportedForSimpleXML();
        }

        // find IN and INOUT parameters
        for (Parameter p : queryOperation.getParameters()) {
            ProcedureArgument arg = findInOutArgument(p.getName());

            // default argument name to parameter name
            String argName = p.getName();
            // override with explicit argument name
            if (arg != null) {
                argName = arg.getName();
            }

            if (arg != null && arg instanceof ProcedureOutputArgument) {
                if (isCursorType(xrService, p.getType())) {
                    throw DBWSException.inoutCursorArgumentsNotSupported();
                }
                spCall.addNamedInOutputArgument(argName, p.getName());
            } else {
                spCall.addNamedArgument(argName, p.getName());
            }
        }

        // find OUT parameters
        if (queryOperation.hasResponse()) {
            if (!queryOperation.isSimpleXMLFormat() ||
                (spCall.isStoredFunctionCall() && !isCursorType(xrService, resultType))) {
                setSingleResult(xrService, spCall, resultType);
            }
            else {
                if (spCall.isStoredFunctionCall() && isCursorType(xrService, resultType)) {
                    spCall.useUnnamedCursorOutputAsResultSet();
                }
                else if (getOutArguments().isEmpty()) {
                    spCall.setReturnsResultSet(true);
                }
                else {
                    for (ProcedureOutputArgument arg : getOutArguments()) {
                        // use argument type
                        if (arg.getResultType() != null && isCursorType(xrService, arg.getResultType())) {
                            spCall.useNamedCursorOutputAsResultSet(arg.getName());
                        } else {
                            spCall.addNamedOutputArgument(arg.getName());
                        }
                    }
                }
            }
        }
        databaseQuery.setCall(spCall);
    }

    @Override
    public void initializeArguments(XRServiceAdapter xrService, QueryOperation queryOperation,
        DatabaseQuery databaseQuery) {
        for (Parameter p : queryOperation.getParameters()) {
            databaseQuery.addArgument(p.getName());
        }
    }

    protected void setSingleResult(XRServiceAdapter xrService, StoredProcedureCall spCall, QName resultType) {
        if (getOutArguments().size() == 1) {
            ProcedureArgument arg = getOutArguments().get(0);
            // use query return type
            if (isCursorType(xrService, resultType)) {
                spCall.useNamedCursorOutputAsResultSet(arg.getName());
            } else {
                spCall.addNamedOutputArgument(arg.getName());
            }
        }
    }

    protected StoredProcedureCall createCall() {
        StoredProcedureCall spCall = new StoredProcedureCall();
        return spCall;
    }

    private ProcedureArgument findInOutArgument(String name) {
        for (ProcedureArgument arg : getInArguments()) {
            if (arg.getParameterName() != null && arg.getParameterName().equalsIgnoreCase(name)) {
                return arg;
            }
            if (arg.getName().equalsIgnoreCase(name)) {
                return arg;
            }
        }
        for (ProcedureArgument arg : getInOutArguments()) {
            if (arg.getParameterName() != null && arg.getParameterName().equalsIgnoreCase(name)) {
                return arg;
            }
            if (arg.getName().equalsIgnoreCase(name)) {
                return arg;
            }
        }

        return null;
    }

    protected boolean isCursorType(@SuppressWarnings("unused") XRServiceAdapter xrService, QName type) {
        if (type.getLocalPart().startsWith("cursor of")) {
            return true;
        }
        return false;
    }

}
