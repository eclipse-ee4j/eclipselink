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
import java.util.Iterator;
import java.util.Map;

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
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.PersistenceUnit;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;

@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Path("/{context}/metadata/")
public class PersistenceUnitResource extends AbstractResource {

    @GET
    @Path("entity/{descriptorAlias}")
    public Response getDescriptorMetadata(@PathParam("context") String persistenceUnit, @PathParam("descriptorAlias") String descriptorAlias, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        return getDescriptorMetadata(persistenceUnit, descriptorAlias, hh, uriInfo.getBaseUri());
    }

    @GET
    public Response getTypes(@PathParam("context") String persistenceUnit, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        return getTypes(persistenceUnit, hh, uriInfo.getBaseUri());
    }

    @GET
    @Path("query")
    public Response getQueriesMetadata(@PathParam("context") String persistenceUnit, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        return getQueriesMetadata(persistenceUnit, hh, uriInfo.getBaseUri());
    }

    @GET
    @Path("query/{queryName}")
    public Response getQueryMetadata(@PathParam("context") String persistenceUnit, @PathParam("queryName") String queryName, @Context HttpHeaders hh, @Context UriInfo uriInfo) {
        return getQueryMetadata(persistenceUnit, queryName, hh, uriInfo.getBaseUri());
    }

    @SuppressWarnings("rawtypes")
    public Response getTypes(String persistenceUnit, HttpHeaders hh, URI baseURI) {
        PersistenceContext app = getPersistenceFactory().get(persistenceUnit, baseURI, null);
        if (app == null) {
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            return Response.status(Status.NOT_FOUND).build();
        } else {
            PersistenceUnit pu = new PersistenceUnit();
            pu.setPersistenceUnitName(persistenceUnit);
            Map<Class, ClassDescriptor> descriptors = app.getJpaSession().getDescriptors();
            String mediaType = StreamingOutputMarshaller.mediaType(hh.getAcceptableMediaTypes()).toString();
            Iterator<Class> contextIterator = descriptors.keySet().iterator();
            while (contextIterator.hasNext()) {
                ClassDescriptor descriptor = descriptors.get(contextIterator.next());
                pu.getTypes().add(new Link(descriptor.getAlias(), mediaType, baseURI + persistenceUnit + "/metadata/entity/" + descriptor.getAlias()));
            }
            String result = null;
            try {
                result = marshallMetadata(pu, mediaType);
            } catch (JAXBException e) {
                JPARSLogger.fine("exception_marshalling_persitence_unit", new Object[] { persistenceUnit, e.toString() });
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }
            ResponseBuilder rb = Response.ok(new StreamingOutputMarshaller(null, result, hh.getAcceptableMediaTypes()));
            rb.header("Content-Type", MediaType.APPLICATION_JSON);
            return rb.build();
        }
    }
}
