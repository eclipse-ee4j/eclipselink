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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.persistence.jpa.rs.resources.common.AbstractQueryResource;
/**
 * PersistenceResource
 *  
 * @deprecated Use {@link  org.eclipse.persistence.jpa.rs.resources.QueryResource} instead.  
 * 
 */
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Path("/{context}/query/")
public class QueryResource extends AbstractQueryResource {

    @POST
    @Path("{name}")
    public Response namedQueryUpdate(@PathParam("context") String persistenceUnit, @PathParam("name") String name, @Context HttpHeaders hh, @Context UriInfo ui) {
        return namedQueryUpdateInternal(null, persistenceUnit, name, hh, ui);
    }
    
    @GET
    @Path("{name}")
    public Response namedQuery(@PathParam("context") String persistenceUnit, @PathParam("name") String name, @Context HttpHeaders hh, @Context UriInfo ui) {
        return namedQueryInternal(null, persistenceUnit, name, hh, ui);
    }
}
