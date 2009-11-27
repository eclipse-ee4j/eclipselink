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

package org.eclipse.persistence.testing.models.wdf.jpa2.flight;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
public class FlightId {

    @Embedded
    private ConnectionId connectionId;

    @Column(name = "FLIGHT_DATE")
    private java.sql.Date date;

    public ConnectionId getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(ConnectionId connectionId) {
        this.connectionId = connectionId;
    }

    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(java.sql.Date date) {
        this.date = date;
    }

}
