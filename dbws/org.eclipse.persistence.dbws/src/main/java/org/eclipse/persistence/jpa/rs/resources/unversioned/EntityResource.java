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

import java.io.InputStream;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.persistence.jpa.rs.resources.common.AbstractEntityResource;

/**
 * @deprecated Use {@link  org.eclipse.persistence.jpa.rs.resources.EntityResource} instead.
 *
 */
@Deprecated
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Path("/{context}/entity/")
public class EntityResource extends AbstractEntityResource {

    @GET
    @Path("{type}/{id}/{attribute}")
    public Response findAttribute(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("id") String id, @PathParam("attribute") String attribute, @Context HttpHeaders hh, @Context UriInfo ui) {
        setRequestUniqueId();
        return findAttributeInternal(null, persistenceUnit, type, id, attribute, hh, ui);
    }

    @GET
    @Path("{type}/{id}")
    public Response find(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("id") String id, @Context HttpHeaders hh, @Context UriInfo ui) {
        setRequestUniqueId();
        return findInternal(null, persistenceUnit, type, id, hh, ui);
    }

    @PUT
    @Path("{type}")
    public Response create(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @Context HttpHeaders hh, @Context UriInfo uriInfo, InputStream in) throws Exception {
        setRequestUniqueId();
        return createInternal(null, persistenceUnit, type, hh, uriInfo, in);
    }

    @POST
    @Path("{type}")
    public Response update(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @Context HttpHeaders hh, @Context UriInfo uriInfo, InputStream in) {
        setRequestUniqueId();
        return updateInternal(null, persistenceUnit, type, hh, uriInfo, in);
    }

    @POST
    @Path("{type}/{id}/{attribute}")
    public Response setOrAddAttribute(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("id") String id, @PathParam("attribute") String attribute, @Context HttpHeaders hh, @Context UriInfo ui, InputStream in) {
        setRequestUniqueId();
        return setOrAddAttributeInternal(null, persistenceUnit, type, id, attribute, hh, ui, in);
    }

    @DELETE
    @Path("{type}/{id}/{attribute}")
    public Response removeAttribute(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("id") String id, @PathParam("attribute") String attribute, @Context HttpHeaders hh, @Context UriInfo ui) {
        setRequestUniqueId();
        return removeAttributeInternal(null, persistenceUnit, type, id, attribute, hh, ui);
    }

    @DELETE
    @Path("{type}/{id}")
    public Response delete(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("id") String id, @Context HttpHeaders hh, @Context UriInfo ui) {
        setRequestUniqueId();
        return deleteInternal(null, persistenceUnit, type, id, hh, ui);
    }
}
