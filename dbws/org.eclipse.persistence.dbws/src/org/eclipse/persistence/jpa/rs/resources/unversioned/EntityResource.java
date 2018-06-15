/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.jpa.rs.resources.unversioned;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
