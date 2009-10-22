/*******************************************************************************
 * Copyright (c) 2005, 2009 SAP. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     SAP - initial API and implementation
 ******************************************************************************/

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
