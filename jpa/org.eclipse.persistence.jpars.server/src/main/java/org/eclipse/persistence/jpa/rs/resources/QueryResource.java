/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     gonural - Initial implementation
//     Dmitry Kornilov - JPARS 2.0 related changes
package org.eclipse.persistence.jpa.rs.resources;

import org.eclipse.persistence.jpa.rs.resources.common.AbstractQueryResource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import static org.eclipse.persistence.jpa.rs.resources.common.AbstractResource.SERVICE_VERSION_FORMAT;

/**
 * Query resource.
 *
 * @author gonural
 */
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Path("/{version : " + SERVICE_VERSION_FORMAT + "}/{context}/query/")
public class QueryResource extends AbstractQueryResource {

    @POST
    @Path("{name}")
    public Response namedQueryUpdate(@PathParam("version") String version,
                                     @PathParam("context") String persistenceUnit,
                                     @PathParam("name") String name,
                                     @Context HttpHeaders hh,
                                     @Context UriInfo ui) {
        setRequestUniqueId();
        return namedQueryUpdateInternal(version, persistenceUnit, name, hh, ui);
    }

    @GET
    @Path("{name}")
    public Response namedQuery(@PathParam("version") String version,
                               @PathParam("context") String persistenceUnit,
                               @PathParam("name") String name,
                               @Context HttpHeaders hh,
                               @Context UriInfo ui) {
        setRequestUniqueId();
        return namedQueryInternal(version, persistenceUnit, name, hh, ui);
    }

    @OPTIONS
    @Path("{name}")
    public Response getQueryOptions(@PathParam("version") String version,
                                    @PathParam("context") String persistenceUnit,
                                    @PathParam("name") String queryName,
                                    @Context HttpHeaders httpHeaders,
                                    @Context UriInfo uriInfo) {
        setRequestUniqueId();
        return buildQueryOptionsResponse(version, persistenceUnit, queryName, httpHeaders, uriInfo);
    }
}
