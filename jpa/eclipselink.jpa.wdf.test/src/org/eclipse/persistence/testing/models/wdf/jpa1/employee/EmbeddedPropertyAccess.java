/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2005, 2015 SAP. All rights reserved.
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
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class EmbeddedPropertyAccess {

    private boolean isNull = true;
    private long date; // this field stores the information of the date property
    private long timeH; // timeH and timeL together store the information of the time property
    private long timeL; // timeH and timeL together store the information of the time property

    public EmbeddedPropertyAccess() {
    }

    /**
     * @return Returns the date.
     */
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDate() {
        if (isNull) {
            return null;
        }
        return new Date(date);
    }

    /**
     * @param date
     *            The date to set.
     */
    public void setDate(Date aDate) {
        if (aDate == null) {
            isNull = true;
            return;
        }
        isNull = false;
        this.date = aDate.getTime();
    }

    /**
     * @return Returns the time.
     */
    @Basic
    public long getTime() {
        return timeH + timeL;
    }

    /**
     * @param time
     *            The time to set.
     */
    public void setTime(long time) {
        this.timeH = time & 0xffffffff00000000L;
        this.timeL = time & 0x00000000ffffffffL;
    }
}
