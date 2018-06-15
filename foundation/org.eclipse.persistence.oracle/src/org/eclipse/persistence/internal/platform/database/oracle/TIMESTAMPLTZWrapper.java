/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
    boolean isLtzTimestampInGmt;

    public TIMESTAMPLTZWrapper(Timestamp ts, String zoneId, boolean isLtzTimestampInGmt) {
        this.ts = ts;
        this.zoneId = zoneId;
        this.isLtzTimestampInGmt = isLtzTimestampInGmt;
    }

    public Timestamp getTimestamp() {
        return ts;
    }

    public String getZoneId() {
        return zoneId;
    }

    public boolean isLtzTimestampInGmt() {
        return isLtzTimestampInGmt;
    }
}
