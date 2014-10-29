/*******************************************************************************
 * Copyright (c) 2011, 2014 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     gonural - Initial implementation
 *     Dmitry Kornilov - 'latest' keyword in version support
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.resources;

import org.eclipse.persistence.jpa.rs.resources.common.AbstractSingleResultQueryResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Single result query resource.
 *
 * @author gonural
 */
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_OCTET_STREAM })
@Path("/{version}/{context}/singleResultQuery/")
public class SingleResultQueryResource extends AbstractSingleResultQueryResource {

    @GET
    @Path("{name}")
    public Response namedQuerySingleResult(@PathParam("version") String version,
                                           @PathParam("context") String persistenceUnit,
                                           @PathParam("name") String name,
                                           @Context HttpHeaders hh,
                                           @Context UriInfo ui) {
        setRequestUniqueId();
        return namedQuerySingleResultInternal(version, persistenceUnit, name, hh, ui);
    }
}
