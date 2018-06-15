/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     gonural - Initial implementation
//     Dmitry Kornilov - JPARS 2.0 related changes
package org.eclipse.persistence.jpa.rs.resources;

import org.eclipse.persistence.jpa.rs.resources.common.AbstractQueryResource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
