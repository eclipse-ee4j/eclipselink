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
public class EmbeddedFieldAccess {
    @Temporal(TemporalType.TIMESTAMP)
    protected Date date; // embedded reference type (mutable)
    @Basic
    protected long time; // embedded primitive type

    public EmbeddedFieldAccess() {
    }

    // getter and setter for testing purposes renamed to
    // make them invisible for the implementation
    final public Date retrieveDate() {
        return date;
    }

    final public void changeDate(final Date aDate) {
        date = aDate;
    }

    final public long retrieveTime() {
        return time;
    }

    final public void changeTime(final long aTime) {
        time = aTime;
    }

}
