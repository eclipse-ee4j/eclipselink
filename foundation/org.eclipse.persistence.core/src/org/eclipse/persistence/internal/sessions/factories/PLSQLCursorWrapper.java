/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David McCann - May 15, 2012 - 2.4 - Initial implementation
 ******************************************************************************/
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

    public ComplexDatabaseType getWrappedType() {
        return (ComplexDatabaseType)wrappedDatabaseType;
    }
}
