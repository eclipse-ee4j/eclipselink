/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 *
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.resources;

import static org.eclipse.persistence.jpa.rs.resources.common.AbstractResource.SERVICE_VERSION_FORMAT;

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
 * @author gonural
 *
 */
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Path("/{version : " + SERVICE_VERSION_FORMAT + "}/{context}/entity/")
public class EntityResource extends AbstractEntityResource {

    @GET
    @Path("{type}/{id}/{attribute}")
    public Response findAttribute(@PathParam("version") String version, @PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("id") String id, @PathParam("attribute") String attribute, @Context HttpHeaders hh, @Context UriInfo ui) {
        setRequestUniqueId();
        return findAttribute(version, persistenceUnit, type, id, attribute, hh, ui, ui.getBaseUri());
    }

    @GET
    @Path("{type}/{id}")
    public Response find(@PathParam("version") String version, @PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("id") String id, @Context HttpHeaders hh, @Context UriInfo ui) {
        setRequestUniqueId();
        return find(version, persistenceUnit, type, id, hh, ui, ui.getBaseUri());
    }

    @PUT
    @Path("{type}")
    public Response create(@PathParam("version") String version, @PathParam("context") String persistenceUnit, @PathParam("type") String type, @Context HttpHeaders hh, @Context UriInfo uriInfo, InputStream in) throws Exception {
        setRequestUniqueId();
        return create(version, persistenceUnit, type, hh, uriInfo, uriInfo.getBaseUri(), in);
    }

    @POST
    @Path("{type}")
    public Response update(@PathParam("version") String version, @PathParam("context") String persistenceUnit, @PathParam("type") String type, @Context HttpHeaders hh, @Context UriInfo uriInfo, InputStream in) {
        setRequestUniqueId();
        return update(version, persistenceUnit, type, hh, uriInfo, uriInfo.getBaseUri(), in);
    }

    @POST
    @Path("{type}/{id}/{attribute}")
    public Response setOrAddAttribute(@PathParam("version") String version, @PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("id") String id, @PathParam("attribute") String attribute, @Context HttpHeaders hh, @Context UriInfo ui, InputStream in) {
        setRequestUniqueId();
        return setOrAddAttribute(version, persistenceUnit, type, id, attribute, hh, ui, ui.getBaseUri(), in);
    }

    @DELETE
    @Path("{type}/{id}/{attribute}")
    public Response removeAttribute(@PathParam("version") String version, @PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("id") String id, @PathParam("attribute") String attribute, @Context HttpHeaders hh, @Context UriInfo ui) {
        setRequestUniqueId();
        return removeAttributeInternal(version, persistenceUnit, type, id, attribute, hh, ui);
    }

    @DELETE
    @Path("{type}/{id}")
    public Response delete(@PathParam("version") String version, @PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("id") String id, @Context UriInfo ui) {
        setRequestUniqueId();
        return delete(version, persistenceUnit, type, id, ui, ui.getBaseUri());
    }
}
