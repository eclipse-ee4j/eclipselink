/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.helidon;

import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.eclipse.persistence.testing.helidon.models.MasterEntity;

import io.helidon.microprofile.server.Server;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestMaster {

     private static Server server;

    @BeforeClass
        public static void startServer() throws Exception {
        server = Main.startServer();
    }

    @AfterClass
    public static void stopServer() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void testMasterOneRest() throws Exception {
        Client client = ClientBuilder.newClient();
        MasterEntity masterEntity = client
                .target("http://localhost:8080/master/one")
                .request("application/json")
                .get(MasterEntity.class);
        assertEquals(1L, masterEntity.getId());
        assertEquals("Master 1", masterEntity.getName());
    }

    @Test
    public void testMasterAllRest() throws Exception {
        Client client = ClientBuilder.newClient();
        List<MasterEntity> masterEntities = client
                .target("http://localhost:8080/master/all")
                .request("application/json")
                .get(List.class);
        assertTrue(masterEntities.size() >= 2);
    }

    @Test
    public void testMasterCreateRemoveRest() throws Exception {
        Client client = ClientBuilder.newClient();
        Response response = client
                .target("http://localhost:8080/master/create_remove")
                .request("application/json")
                .get();
    }
}
