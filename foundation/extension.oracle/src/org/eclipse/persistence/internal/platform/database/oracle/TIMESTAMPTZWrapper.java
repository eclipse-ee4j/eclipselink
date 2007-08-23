/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.platform.database.oracle;

import java.io.Serializable;
import java.sql.*;
import java.util.TimeZone;
import oracle.sql.TIMESTAMPTZ;
import org.eclipse.persistence.exceptions.DatabaseException;

/**
 * This class is used as a wrapper for TIMESTAMPTZ.  It stores a Timestamp and a TimeZone.
 * Bug#4364359  This wrapper is added to overcome TIMESTAMPTZ not serializable as of jdbc 9.2.0.5 and 10.1.0.2.  
 * It has been fixed in the next version for both streams
 */
public class TIMESTAMPTZWrapper implements Serializable {
    Timestamp ts;
    TimeZone tz;

    public TIMESTAMPTZWrapper(TIMESTAMPTZ timestampTZ) {
		try {
            ts = timestampTZ.timestampValue(null);
            tz = TIMESTAMPHelper.extractTimeZone(timestampTZ.toBytes());
		} catch (SQLException exception) {
			throw DatabaseException.sqlException(exception);
		}
    }

    public Timestamp getTimestamp() {
        return ts;
    }

    public TimeZone getTimeZone() {
        return tz;
    }
}