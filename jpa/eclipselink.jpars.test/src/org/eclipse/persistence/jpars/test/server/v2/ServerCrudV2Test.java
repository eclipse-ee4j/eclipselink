/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      Dmitry Kornilov - Initial implementation, upgrade to Jersey 2.x
package org.eclipse.persistence.jpars.test.server.v2;

import org.eclipse.persistence.jpars.test.model.auction.StaticAuction;
import org.eclipse.persistence.jpars.test.server.RestCallFailedException;
import org.eclipse.persistence.jpars.test.server.noversion.ServerCrudTest;
import org.eclipse.persistence.jpars.test.util.RestUtils;
import org.eclipse.persistence.jpars.test.util.StaticModelDatabasePopulator;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

/**
 * ServerCrudTest modified for JPARS v2.0.
 * {@see ServerCrudTest}
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0
 */
public class ServerCrudV2Test extends ServerCrudTest {

    @BeforeClass
    public static void setup() throws Exception {
        initContext("jpars_auction-static", "v2.0");
        StaticModelDatabasePopulator.populateDB(emf);
    }

    /**
     * Test create employee with phone numbers non idempotent.
     */
    @Override
    @Test(expected = RestCallFailedException.class)
    public void testCreateAuctionNonIdempotent() throws Exception {
        String auction = RestUtils.getJSONMessage("auction-bidsByValueNoId-V2.json");
        // The bid contained by the auction object has generated id field, and create is idempotent.
        // So, create operation on auction with bid list should fail.
        RestUtils.restCreateWithSequence(context, auction, StaticAuction.class, MediaType.APPLICATION_JSON_TYPE);
    }
}
