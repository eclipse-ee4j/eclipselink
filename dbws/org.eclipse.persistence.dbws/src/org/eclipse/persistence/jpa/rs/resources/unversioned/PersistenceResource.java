/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      dclarke/tware - initial 
 ******************************************************************************/
package org.eclipse.persistence.jpa.rs.resources.unversioned;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jpa.rs.resources.common.AbstractPersistenceResource;

/**
 * PersistenceResource 
 * @deprecated Use {@link  org.eclipse.persistence.jpa.rs.resources.PersistenceResource} instead.  
 * 
 * @author tware
 *
 */
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Path("/")
public class PersistenceResource extends AbstractPersistenceResource {

    @GET
    public Response getContexts(@Context HttpHeaders hh, @Context UriInfo uriInfo) throws JAXBException {
        return getContexts(null, hh, uriInfo.getBaseUri());
    }

    @POST
    @Produces(MediaType.WILDCARD)
    public Response callSessionBean(@Context HttpHeaders hh, @Context UriInfo ui, InputStream is) throws JAXBException, ClassNotFoundException, NamingException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return callSessionBeanInternal(null, hh, ui, is);
    }
}
