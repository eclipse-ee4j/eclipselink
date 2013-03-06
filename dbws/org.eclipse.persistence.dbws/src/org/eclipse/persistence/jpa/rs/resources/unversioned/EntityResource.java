/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 *
 ******************************************************************************/

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
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jpa.rs.resources.common.AbstractEntityResource;

/**
 * @deprecated Use {@link  org.eclipse.persistence.jpa.rs.resources.EntityResource} instead.  
 * 
 */
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Path("/{context}/entity/")
public class EntityResource extends AbstractEntityResource {

    @GET
    @Path("{type}/{key}/{attribute}")
    public Response findAttribute(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("key") String key, @PathParam("attribute") String attribute, @Context HttpHeaders hh, @Context UriInfo ui) {
        return findAttribute(null, persistenceUnit, type, key, attribute, hh, ui, ui.getBaseUri());
    }

    @GET
    @Path("{type}/{key}")
    public Response find(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("key") String key, @Context HttpHeaders hh, @Context UriInfo ui) {
        return find(null, persistenceUnit, type, key, hh, ui, ui.getBaseUri());
    }

    @PUT
    @Path("{type}")
    public Response create(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @Context HttpHeaders hh, @Context UriInfo uriInfo, InputStream in) throws JAXBException {
        return create(null, persistenceUnit, type, hh, uriInfo, uriInfo.getBaseUri(), in);
    }

    @POST
    @Path("{type}")
    public Response update(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @Context HttpHeaders hh, @Context UriInfo uriInfo, InputStream in) {
        return update(null, persistenceUnit, type, hh, uriInfo, uriInfo.getBaseUri(), in);
    }

    @POST
    @Path("{type}/{key}/{attribute}")
    public Response setOrAddAttribute(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("key") String key, @PathParam("attribute") String attribute, @Context HttpHeaders hh, @Context UriInfo ui, InputStream in) {
        return setOrAddAttribute(null, persistenceUnit, type, key, attribute, hh, ui, ui.getBaseUri(), in);
    }

    @DELETE
    @Path("{type}/{key}/{attribute}")
    public Response removeAttribute(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("key") String key, @PathParam("attribute") String attribute,
            @Context HttpHeaders hh, @Context UriInfo ui) {
        return removeAttributeInternal(null, persistenceUnit, type, key, attribute, hh, ui);
    }

    @DELETE
    @Path("{type}/{key}")
    public Response delete(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("key") String key, @Context UriInfo ui, @Context HttpHeaders hh) {
        return delete(null, persistenceUnit, type, key, ui, hh, ui.getBaseUri());
    }
}