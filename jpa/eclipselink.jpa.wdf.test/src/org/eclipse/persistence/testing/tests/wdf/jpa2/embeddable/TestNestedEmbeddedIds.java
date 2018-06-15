/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.tests.wdf.jpa2.embeddable;

import static org.junit.Assert.assertEquals;

import javax.persistence.EntityManager;

import org.eclipse.persistence.testing.framework.wdf.JPAEnvironment;
import org.eclipse.persistence.testing.framework.wdf.Skip;
import org.eclipse.persistence.testing.models.wdf.jpa2.flight.Carrier;
import org.eclipse.persistence.testing.models.wdf.jpa2.flight.Connection;
import org.eclipse.persistence.testing.models.wdf.jpa2.flight.ConnectionId;
import org.eclipse.persistence.testing.models.wdf.jpa2.flight.Flight;
import org.eclipse.persistence.testing.models.wdf.jpa2.flight.FlightId;
import org.eclipse.persistence.testing.tests.wdf.jpa2.JPA2Base;
import org.junit.Test;

public class TestNestedEmbeddedIds extends JPA2Base {


    @Test
    @Skip(server=true)
    public void testInsert() {
        JPAEnvironment env = getEnvironment();
        EntityManager em = env.getEntityManager();
        try {
            env.beginTransaction(em);
            Carrier lh = new Carrier();
            lh.setId("LH");
            lh.setDescription("Lufthansa");
            em.persist(lh);

            Connection lh454 = new Connection();
            ConnectionId connectionId = new ConnectionId();
            connectionId.setCarrier("LH");
            connectionId.setFlightNumber(454);
            lh454.setId(connectionId);
            lh454.setFrom("FRA");
            lh454.setTo("SFO");
            em.persist(lh454);

            Flight flight090323 = new Flight();
            FlightId flightId = new FlightId();
            flightId.setConnectionId(connectionId);
            flightId.setDate(java.sql.Date.valueOf("2009-03-23"));
            flight090323.setFlightId(flightId);
            flight090323.setPassengers(223);
            em.persist(flight090323);

            env.commitTransactionAndClear(em);

            Flight flight2 = em.find(Flight.class, flightId);

            assertEquals(flight2.getFlightId().getConnectionId().getCarrier(), "LH");



        } finally {
            closeEntityManager(em);
        }
    }


}
