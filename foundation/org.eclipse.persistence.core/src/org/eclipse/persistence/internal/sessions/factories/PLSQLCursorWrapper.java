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
//     David McCann - May 15, 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.internal.sessions.factories;

import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseType;

/**
 * <b>INTERNAL</b>: a helper class that holds DatabaseTypes.
 * Used to support marshalling PLSQLStoredProcedureCalls
 */
public class PLSQLCursorWrapper extends DatabaseTypeWrapper {

    public PLSQLCursorWrapper() {
        super();
    }

    public PLSQLCursorWrapper(DatabaseType wrappedType) {
        super(wrappedType);
    }

    @Override
    public ComplexDatabaseType getWrappedType() {
        return (ComplexDatabaseType)wrappedDatabaseType;
    }
}
