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
package org.eclipse.persistence.jpa.rs;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;

@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Path("/{context}/singleResultQuery/")
public class SingleResultQueryResource extends AbstractResource {

    @GET
    @Path("{name}")
    @Produces(MediaType.WILDCARD)
    public Response namedQuerySingleResult(@PathParam("context") String persistenceUnit, @PathParam("name") String name, @Context HttpHeaders hh, @Context UriInfo ui) {
        return namedQuerySingleResult(persistenceUnit, name, hh, ui, ui.getBaseUri());
    }
    
    protected Response namedQuerySingleResult(String persistenceUnit, String name, HttpHeaders hh, UriInfo ui, URI baseURI) {
        PersistenceContext app = getPersistenceFactory().get(persistenceUnit, baseURI, null);
        if (app == null) {
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            return Response.status(Status.NOT_FOUND).build();
        }
        Object result = app.querySingleResult(getParameterMap(ui, persistenceUnit), name, getParameterMap(ui, name), getHintMap(ui));
        return Response.ok(new StreamingOutputMarshaller(app, result, hh.getAcceptableMediaTypes())).build();
    }
}
