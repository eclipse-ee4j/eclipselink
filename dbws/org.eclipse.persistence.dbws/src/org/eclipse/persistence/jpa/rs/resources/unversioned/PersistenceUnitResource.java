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
package org.eclipse.persistence.jpa.rs.resources.unversioned;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.persistence.jpa.rs.resources.common.AbstractPersistenceUnitResource;

/**
 * PersistenceResource
 *  
 * @deprecated Use {@link  org.eclipse.persistence.jpa.rs.resources.PersistenceUnitResource} instead.  
 * 
 */
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Path("/{context}/metadata/")
public class PersistenceUnitResource extends AbstractPersistenceUnitResource {

    @GET
    @Path("entity/{descriptorAlias}")
    public Response getDescriptorMetadata(@PathParam("context") String persistenceUnit, @PathParam("descriptorAlias") String descriptorAlias, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        setRequestUniqueId();
        return getDescriptorMetadata(null, persistenceUnit, descriptorAlias, hh, uriInfo.getBaseUri());
    }

    @GET
    public Response getTypes(@PathParam("context") String persistenceUnit, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        setRequestUniqueId();
        return getTypes(null, persistenceUnit, hh, uriInfo.getBaseUri());
    }

    @GET
    @Path("query")
    public Response getQueriesMetadata(@PathParam("context") String persistenceUnit, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        setRequestUniqueId();
        return getQueriesMetadata(null, persistenceUnit, hh, uriInfo.getBaseUri());
    }

    @GET
    @Path("query/{queryName}")
    public Response getQueryMetadata(@PathParam("context") String persistenceUnit, @PathParam("queryName") String queryName, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        setRequestUniqueId();
        return getQueryMetadata(null, persistenceUnit, queryName, hh, uriInfo.getBaseUri());
    }
}
