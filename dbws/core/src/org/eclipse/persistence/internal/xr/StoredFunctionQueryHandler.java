/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

package org.eclipse.persistence.internal.xr;

// Javase imports

// Java extension imports
import javax.xml.namespace.QName;

// TopLink imports
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.StoredFunctionCall;
import org.eclipse.persistence.queries.StoredProcedureCall;

/**
 * <p><b>INTERNAL:</b> StoredFunctionQueryHandler sets up the StoredFunctionCall
 * and its arguments in the given {@link DatabaseQuery}
 * 
 * @author Mike Norman - michael.norman@oracle.com
 * @since Oracle TopLink 11.x.x
 */

public class StoredFunctionQueryHandler extends StoredProcedureQueryHandler {

    @Override
    protected StoredProcedureCall createCall() {
        return new StoredFunctionCall();
    }

    @Override
    protected void setSingleResult(XRServiceAdapter xrService, StoredProcedureCall spCall, QName resultType) {
        if (isCursorType(xrService, resultType)) {
            spCall.useUnnamedCursorOutputAsResultSet();
        }
    }
}
