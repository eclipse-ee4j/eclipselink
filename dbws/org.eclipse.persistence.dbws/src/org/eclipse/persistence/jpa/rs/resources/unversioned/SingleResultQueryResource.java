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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.persistence.jpa.rs.resources.common.AbstractSingleResultQueryResource;
/**
 * PersistenceResource
 *  
 * @deprecated Use {@link  org.eclipse.persistence.jpa.rs.resources.SingleResultQueryResource} instead.  
 * 
 */
// Fix for Bug 393320 - JPA-RS: Respect the Accept Header for a singleResultQuery 
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,  MediaType.APPLICATION_OCTET_STREAM})
@Path("/{context}/singleResultQuery/")
public class SingleResultQueryResource extends AbstractSingleResultQueryResource {
    @GET
    @Path("{name}")
    public Response namedQuerySingleResult(@PathParam("context") String persistenceUnit, @PathParam("name") String name, @Context HttpHeaders hh, @Context UriInfo ui) {
        return namedQuerySingleResult(null, persistenceUnit, name, hh, ui, ui.getBaseUri());
    }
}