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
package org.eclipse.persistence.testing.helidon.resource;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.persistence.testing.helidon.models.MasterEntity;
import org.eclipse.persistence.testing.helidon.provider.MasterProvider;

@Path("/master")
public class MasterResource {

    @Inject
    MasterProvider masterProvider;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("one")
    public MasterEntity masterOne() {
        return masterProvider.getMasterOne();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public List<MasterEntity> masterAll() {
        return masterProvider.getMasterAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("create_remove")
    public void createRemove() {
        masterProvider.createRemove();;
    }

}
