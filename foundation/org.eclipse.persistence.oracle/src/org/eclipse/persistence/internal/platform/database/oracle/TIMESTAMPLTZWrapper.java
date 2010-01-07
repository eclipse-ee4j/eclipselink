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
package org.eclipse.persistence.internal.platform.database.oracle;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * This class is used as a wrapper for TIMESTAMPLTZ.  It stores a Timestamp and a timezone id.
 * Bug#4364359  This wrapper is added to overcome TIMESTAMPLTZ not serializable as of jdbc 9.2.0.5 and 10.1.0.2.  
 * It has been fixed in the next version for both streams
 */
public class TIMESTAMPLTZWrapper implements Serializable {
    Timestamp ts;
    String zoneId;

    public TIMESTAMPLTZWrapper(Timestamp ts, String zoneId) {
        this.ts = ts;
        this.zoneId = zoneId;
    }

    public Timestamp getTimestamp() {
        return ts;
    }

    public String getZoneId() {
        return zoneId;
    }
}
