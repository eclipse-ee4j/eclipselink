/*******************************************************************************
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David McCann - December 09, 2011 - 2.4 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.sessions.factories;

import org.eclipse.persistence.internal.helper.ComplexDatabaseType;
import org.eclipse.persistence.internal.helper.DatabaseType;

/**
 * <b>INTERNAL</b>: a helper class that holds DatabaseType's. Used to support marshalling
 * PLSQLStoredProcedureCall's
 */
public class OracleObjectTypeWrapper extends DatabaseTypeWrapper {

    public OracleObjectTypeWrapper() {
        super();
    }

    public OracleObjectTypeWrapper(DatabaseType wrappedType) {
        super(wrappedType);
    }

    @Override
    public ComplexDatabaseType getWrappedType() {
        return (ComplexDatabaseType)wrappedDatabaseType;
    }
}
