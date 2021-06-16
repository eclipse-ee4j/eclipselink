/*
 * Copyright (c) 2005, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     SAP - initial API and implementation

package org.eclipse.persistence.testing.models.wdf.jpa1.employee;

import java.util.Date;

import jakarta.persistence.Basic;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

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
