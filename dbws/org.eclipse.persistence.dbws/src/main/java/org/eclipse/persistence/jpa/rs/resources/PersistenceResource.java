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

import org.eclipse.persistence.jpa.rs.resources.common.AbstractPersistenceResource;

import javax.naming.NamingException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.xml.bind.JAXBException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import static org.eclipse.persistence.jpa.rs.resources.common.AbstractResource.SERVICE_VERSION_FORMAT;

/**
 * Persistence units catalog resource (JPARS version 2.0 and above).
 *
 * @author gonural
 */
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Path("/{version : " + SERVICE_VERSION_FORMAT + "}/")
public class PersistenceResource extends AbstractPersistenceResource {

    @GET
    public Response getContexts(@PathParam("version") String version,
                                @Context HttpHeaders hh,
                                @Context UriInfo uriInfo) throws JAXBException {
        setRequestUniqueId();
        return getContextsInternal(version, hh, uriInfo);
    }

    @POST
    @Produces(MediaType.WILDCARD)
    public Response callSessionBean(@PathParam("version") String version,
                                    @Context HttpHeaders hh,
                                    @Context UriInfo ui,
                                    InputStream is) throws JAXBException, ClassNotFoundException, NamingException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        setRequestUniqueId();
        return callSessionBeanInternal(version, hh, ui, is);
    }
}
