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

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
