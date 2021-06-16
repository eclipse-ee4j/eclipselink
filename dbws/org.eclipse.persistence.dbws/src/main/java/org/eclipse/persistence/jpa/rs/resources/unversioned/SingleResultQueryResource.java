/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.jpa.rs.resources.unversioned;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.persistence.jpa.rs.resources.common.AbstractSingleResultQueryResource;

/**
 * PersistenceResource
 *
 * @deprecated Use {@link  org.eclipse.persistence.jpa.rs.resources.SingleResultQueryResource} instead.
 *
 */
@Deprecated
// Fix for Bug 393320 - JPA-RS: Respect the Accept Header for a singleResultQuery
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_OCTET_STREAM })
@Path("/{context}/singleResultQuery/")
public class SingleResultQueryResource extends AbstractSingleResultQueryResource {
    @GET
    @Path("{name}")
    public Response namedQuerySingleResult(@PathParam("context") String persistenceUnit, @PathParam("name") String name, @Context HttpHeaders hh, @Context UriInfo ui) {
        setRequestUniqueId();
        return namedQuerySingleResultInternal(null, persistenceUnit, name, hh, ui);
    }
}
