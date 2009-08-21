/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
package org.eclipse.persistence.platform.database.oracle;

import java.sql.ResultSet;

import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose:</b>
 * Supports usage of certain Oracle JDBC specific APIs for the Oracle 11 database.
 */
public class Oracle11Platform extends Oracle10Platform {
    /* NCLOB sql type is defined in OracleTypes starting with Oracle jdbc version 11.
     * The constant redefined here because for backward compatibility:
     * OracleTypes.NCLOB won't compile with Oracle jdbc 10 and earlier.
     */
    public final static int OracleTypes_NCLOB = 2011; 
    /**
     * INTERNAL:
     * Get a timestamp value from a result set.
     * Overrides the default behavior to specifically return a timestamp.  Added
     * to overcome an issue with the oracle 9.0.1.4 JDBC driver.
     */
    public Object getObjectFromResultSet(ResultSet resultSet, int columnNumber, int type, AbstractSession session) throws java.sql.SQLException {
        Object value = super.getObjectFromResultSet(resultSet, columnNumber, type, session);
        if(type == OracleTypes_NCLOB) {
            value = convertObject(value, ClassConstants.STRING);
        }
        return value;
    }
}
