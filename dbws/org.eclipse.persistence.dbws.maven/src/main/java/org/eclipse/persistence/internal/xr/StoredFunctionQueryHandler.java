/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink

package org.eclipse.persistence.internal.xr;

// Javase imports

// Java extension imports
import javax.xml.namespace.QName;

// EclipseLink imports
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.StoredFunctionCall;
import org.eclipse.persistence.queries.StoredProcedureCall;
import static org.eclipse.persistence.internal.helper.ClassConstants.OBJECT;
import static org.eclipse.persistence.internal.xr.Util.SCHEMA_2_CLASS;

/**
 * <p><b>INTERNAL:</b> StoredFunctionQueryHandler sets up the StoredFunctionCall
 * and its arguments in the given {@link DatabaseQuery}
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */

public class StoredFunctionQueryHandler extends StoredProcedureQueryHandler {

    @Override
    public boolean isStoredFunctionQueryHandler() {
        return true;
    }

    @Override
    protected StoredProcedureCall createCall() {
        return new StoredFunctionCall();
    }

    @Override
    protected void setSingleResult(XRServiceAdapter xrService, StoredProcedureCall spCall, QName resultType) {
        if (isCursorType(xrService, resultType)) {
            spCall.useUnnamedCursorOutputAsResultSet();
        }
        else {
            StoredFunctionCall sfCall = (StoredFunctionCall)spCall;
            Class<?> clz = SCHEMA_2_CLASS.get(resultType);
            if (clz != null) {
                sfCall.setResult("", clz);
            }
            else {
                sfCall.setResult("", OBJECT);
            }
            DatabasePlatform platform = xrService.getORSession().getPlatform();
            if (platform == null) {
                platform = new DatabasePlatform();
            }
            // StoredFunction's return value is the first parameter
            ((DatabaseField)sfCall.getParameters().get(0)).setSqlType(
                platform.getJDBCType(clz));
        }
    }
}
