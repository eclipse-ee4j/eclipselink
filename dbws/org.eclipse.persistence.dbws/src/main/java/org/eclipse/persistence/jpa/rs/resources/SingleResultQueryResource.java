/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     gonural - Initial implementation
//     Dmitry Kornilov - 'latest' keyword in version support
package org.eclipse.persistence.jpa.rs.resources;

import org.eclipse.persistence.jpa.rs.resources.common.AbstractSingleResultQueryResource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import static org.eclipse.persistence.jpa.rs.resources.common.AbstractResource.SERVICE_VERSION_FORMAT;

/**
 * Single result query resource.
 *
 * @author gonural
 */
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_OCTET_STREAM })
@Path("/{version : " + SERVICE_VERSION_FORMAT + "}/{context}/singleResultQuery/")
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
