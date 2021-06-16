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

package org.eclipse.persistence.testing.models.wdf.jpa2.flight;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "TMP2_FLIGHT")
public class Flight {

    public FlightId getFlightId() {
        return flightId;
    }

    public int getPassengers() {
        return passengers;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setFlightId(FlightId flightId) {
        this.flightId = flightId;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @EmbeddedId
    FlightId flightId;

    int passengers;

    @ManyToOne
    @JoinColumns( {
            @JoinColumn(name = "CARRIER_ID", referencedColumnName = "CARRIER_ID", insertable = false, updatable = false),
            @JoinColumn(name = "FLIGHT_NUMBER", referencedColumnName = "FLIGHT_NUMBER", insertable = false, updatable = false) })
    Connection connection;

}
