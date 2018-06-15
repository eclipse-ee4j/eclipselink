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
@Deprecated
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Path("/{context}/metadata/")
public class PersistenceUnitResource extends AbstractPersistenceUnitResource {

    @GET
    @Path("entity/{descriptorAlias}")
    public Response getDescriptorMetadata(@PathParam("context") String persistenceUnit, @PathParam("descriptorAlias") String descriptorAlias, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        setRequestUniqueId();
        return getDescriptorMetadataInternal(null, persistenceUnit, descriptorAlias, hh, uriInfo);
    }

    @GET
    public Response getTypes(@PathParam("context") String persistenceUnit, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        setRequestUniqueId();
        return getTypesInternal(null, persistenceUnit, hh, uriInfo);
    }

    @GET
    @Path("query")
    public Response getQueriesMetadata(@PathParam("context") String persistenceUnit, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        setRequestUniqueId();
        return getQueriesMetadataInternal(null, persistenceUnit, hh, uriInfo);
    }

    @GET
    @Path("query/{queryName}")
    public Response getQueryMetadata(@PathParam("context") String persistenceUnit, @PathParam("queryName") String queryName, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        setRequestUniqueId();
        return getQueryMetadataInternal(null, persistenceUnit, queryName, hh, uriInfo);
    }
}
