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

import org.eclipse.persistence.jpa.rs.resources.common.AbstractEntityResource;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
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
import java.io.InputStream;

import static org.eclipse.persistence.jpa.rs.resources.common.AbstractResource.SERVICE_VERSION_FORMAT;

/**
 * Entity resource.
 *
 * @author gonural
 */
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Path("/{version : " + SERVICE_VERSION_FORMAT + "}/{context}/entity/")
public class EntityResource extends AbstractEntityResource {

    @GET
    @Path("{type}/{id}/{attribute}")
    public Response findAttribute(@PathParam("version") String version,
                                  @PathParam("context") String persistenceUnit,
                                  @PathParam("type") String type,
                                  @PathParam("id") String id,
                                  @PathParam("attribute") String attribute,
                                  @Context HttpHeaders hh,
                                  @Context UriInfo ui) {
        setRequestUniqueId();
        return findAttributeInternal(version, persistenceUnit, type, id, attribute, hh, ui);
    }

    @GET
    @Path("{type}/{id}")
    public Response find(@PathParam("version") String version,
                         @PathParam("context") String persistenceUnit,
                         @PathParam("type") String type,
                         @PathParam("id") String id,
                         @Context HttpHeaders hh,
                         @Context UriInfo ui) {
        setRequestUniqueId();
        return findInternal(version, persistenceUnit, type, id, hh, ui);
    }

    @PUT
    @Path("{type}")
    public Response create(@PathParam("version") String version,
                           @PathParam("context") String persistenceUnit,
                           @PathParam("type") String type,
                           @Context HttpHeaders hh,
                           @Context UriInfo uriInfo,
                           InputStream in) throws Exception {
        setRequestUniqueId();
        return createInternal(version, persistenceUnit, type, hh, uriInfo, in);
    }

    @POST
    @Path("{type}")
    public Response update(@PathParam("version") String version,
                           @PathParam("context") String persistenceUnit,
                           @PathParam("type") String type,
                           @Context HttpHeaders hh,
                           @Context UriInfo uriInfo,
                           InputStream in) {
        setRequestUniqueId();
        return updateInternal(version, persistenceUnit, type, hh, uriInfo, in);
    }

    @POST
    @Path("{type}/{id}/{attribute}")
    public Response setOrAddAttribute(@PathParam("version") String version,
                                      @PathParam("context") String persistenceUnit,
                                      @PathParam("type") String type,
                                      @PathParam("id") String id,
                                      @PathParam("attribute") String attribute,
                                      @Context HttpHeaders hh,
                                      @Context UriInfo ui, InputStream in) {
        setRequestUniqueId();
        return setOrAddAttributeInternal(version, persistenceUnit, type, id, attribute, hh, ui, in);
    }

    @DELETE
    @Path("{type}/{id}/{attribute}")
    public Response removeAttribute(@PathParam("version") String version,
                                    @PathParam("context") String persistenceUnit,
                                    @PathParam("type") String type,
                                    @PathParam("id") String id,
                                    @PathParam("attribute") String attribute,
                                    @Context HttpHeaders hh,
                                    @Context UriInfo ui) {
        setRequestUniqueId();
        return removeAttributeInternal(version, persistenceUnit, type, id, attribute, hh, ui);
    }

    @DELETE
    @Path("{type}/{id}")
    public Response delete(@PathParam("version") String version,
                           @PathParam("context") String persistenceUnit,
                           @PathParam("type") String type,
                           @PathParam("id") String id,
                           @Context HttpHeaders hh,
                           @Context UriInfo ui) {
        setRequestUniqueId();
        return deleteInternal(version, persistenceUnit, type, id, hh, ui);
    }

    @OPTIONS
    @Path("{entityName}")
    public Response getEntityOptions(@PathParam("version") String version,
                                     @PathParam("context") String persistenceUnit,
                                     @PathParam("entityName") String entityName,
                                     @Context HttpHeaders httpHeaders,
                                     @Context UriInfo uriInfo) {
        setRequestUniqueId();
        return buildEntityOptionsResponse(version, persistenceUnit, entityName, httpHeaders, uriInfo);
    }
}
