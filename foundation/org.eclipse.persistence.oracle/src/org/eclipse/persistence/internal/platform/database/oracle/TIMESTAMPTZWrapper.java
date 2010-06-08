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
 *     dminsky - removed Timestamp & TimeZone retrieval code from constructor 
 ******************************************************************************/  
package org.eclipse.persistence.internal.platform.database.oracle;

import java.io.Serializable;
import java.sql.*;
import java.util.TimeZone;

/**
 * This class is used as a wrapper for TIMESTAMPTZ.  It stores a Timestamp and a TimeZone.
 * Bug#4364359  This wrapper is added to overcome TIMESTAMPTZ not serializable as of jdbc 9.2.0.5 and 10.1.0.2.  
 * It has been fixed in the next version for both streams
 */
public class TIMESTAMPTZWrapper implements Serializable {
    Timestamp ts;
    TimeZone tz;
    boolean isTimestampInGmt;

    public TIMESTAMPTZWrapper(Timestamp ts, TimeZone tz, boolean isTimestampInGmt) {
        // EL Bug 288858 - removed Timestamp & TimeZone retrieval code
        this.ts = ts;
        this.tz = tz;
        this.isTimestampInGmt = isTimestampInGmt;
    }

    public Timestamp getTimestamp() {
        return ts;
    }

    public TimeZone getTimeZone() {
        return tz;
    }
    
    public boolean isTimestampInGmt() {
        return isTimestampInGmt;
    }
}
