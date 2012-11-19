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

import static org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller.mediaType;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import org.eclipse.persistence.jpa.rs.util.IdHelper;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;

@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Path("/{context}/entity/")
public class EntityResource extends AbstractResource {

    public static final String RELATIONSHIP_PARTNER = "partner";
    
    @GET
    @Path("{type}/{key}/{attribute}")
    public Response findAttribute(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("key") String key, @PathParam("attribute") String attribute,
            @Context HttpHeaders hh, @Context UriInfo ui) {
        return findAttribute(persistenceUnit, type, key, attribute, hh, ui, ui.getBaseUri());
    }
    
    protected Response findAttribute(String persistenceUnit, String type, String key, String attribute, HttpHeaders hh, UriInfo ui, URI baseURI) {
        PersistenceContext app = getPersistenceFactory().get(persistenceUnit, baseURI, null);
        if (app == null || app.getClass(type) == null) {
            if (app == null) {
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
            }
            return Response.status(Status.NOT_FOUND).build();
        }
        Map<String, String> discriminators = getParameterMap(ui, persistenceUnit);
        Object id = IdHelper.buildId(app, type, key, discriminators);

        Object entity = app.findAttribute(discriminators, type, id, getHintMap(ui), attribute);

        if (entity == null) {
            JPARSLogger.fine("jpars_could_not_entity_for_attribute", new Object[] { type, key, attribute, persistenceUnit });
            return Response.status(Status.NOT_FOUND).build();
        } else {
            return Response.ok(new StreamingOutputMarshaller(app, entity, hh.getAcceptableMediaTypes())).build();
        }
    }

    @GET
    @Path("{type}/{key}")
    public Response find(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("key") String key, @Context HttpHeaders hh, @Context UriInfo ui) {
        return find(persistenceUnit, type, key, hh, ui, ui.getBaseUri());
    }


    protected Response find(String persistenceUnit, String type, String key, HttpHeaders hh, UriInfo ui, URI baseURI) {
        PersistenceContext app = getPersistenceFactory().get(persistenceUnit, baseURI, null);
        if (app == null || app.getClass(type) == null) {
            if (app == null) {
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
            }
            return Response.status(Status.NOT_FOUND).build();
        }
        Map<String, String> discriminators = getParameterMap(ui, persistenceUnit);

        Object id = IdHelper.buildId(app, type, key, discriminators);

        Object entity = app.find(discriminators, type, id, getHintMap(ui));

        if (entity == null) {
            JPARSLogger.fine("jpars_could_not_entity_for_key", new Object[] { type, key, persistenceUnit });
            return Response.status(Status.NOT_FOUND).build();
        } else {
            return Response.ok(new StreamingOutputMarshaller(app, entity, hh.getAcceptableMediaTypes())).build();
        }
    }
    
    @PUT
    @Path("{type}")
    public Response create(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @Context HttpHeaders hh, @Context UriInfo uriInfo, InputStream in) throws JAXBException {
        return create(persistenceUnit, type, hh, uriInfo, uriInfo.getBaseUri(), in);
    }

    protected Response create(String persistenceUnit, String type, HttpHeaders hh, UriInfo uriInfo, URI baseURI, InputStream in) throws JAXBException {
        PersistenceContext app = getPersistenceFactory().get(persistenceUnit, baseURI, null);
        if (app == null) {
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            return Response.status(Status.NOT_FOUND).build();
        }
        ClassDescriptor descriptor = app.getDescriptor(type);
        if (descriptor == null) {
            JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
            return Response.status(Status.NOT_FOUND).build();
        }
        Object entity = null;
        try {
            entity = app.unmarshalEntity(type, mediaType(hh.getAcceptableMediaTypes()), in);
        } catch (JAXBException e) {
            JPARSLogger.fine("exception_while_unmarhalling_entity", new Object[] { type, persistenceUnit, e.toString() });
            return Response.status(Status.BAD_REQUEST).build();
        }

        // maintain idempotence on PUT by disallowing sequencing
        AbstractDirectMapping sequenceMapping = descriptor.getObjectBuilder().getSequenceMapping();
        if (sequenceMapping != null) {
            Object value = sequenceMapping.getAttributeAccessor().getAttributeValueFromObject(entity);

            if (descriptor.getObjectBuilder().isPrimaryKeyComponentInvalid(value, descriptor.getPrimaryKeyFields().indexOf(descriptor.getSequenceNumberField())) || descriptor.getSequence().shouldAlwaysOverrideExistingValue()) {
                JPARSLogger.fine("jpars_put_not_idempotent", new Object[] { type, persistenceUnit });
                return Response.status(Status.BAD_REQUEST).build();
            }
        }

        app.create(getParameterMap(uriInfo, persistenceUnit), entity);
        ResponseBuilder rb = Response.status(Status.CREATED);
        rb.entity(new StreamingOutputMarshaller(app, entity, hh.getAcceptableMediaTypes()));
        return rb.build();
    }
    
    @POST
    @Path("{type}")
    public Response update(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @Context HttpHeaders hh, @Context UriInfo uriInfo, InputStream in) {
        return update(persistenceUnit, type, hh, uriInfo, uriInfo.getBaseUri(), in);
    }

    protected Response update(String persistenceUnit, String type, HttpHeaders hh, UriInfo uriInfo, URI baseURI, InputStream in) {
        PersistenceContext app = getPersistenceFactory().get(persistenceUnit, baseURI, null);
        if (app == null || app.getClass(type) == null) {
            if (app == null) {
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
            }
            return Response.status(Status.NOT_FOUND).build();
        }
        MediaType contentType = mediaType(hh.getRequestHeader(HttpHeaders.CONTENT_TYPE));
        Object entity = null;
        try {
            entity = app.unmarshalEntity(type, contentType, in);
        } catch (JAXBException e) {
            JPARSLogger.fine("exception_while_unmarhalling_entity", new Object[] { type, persistenceUnit, e.toString() });
            return Response.status(Status.BAD_REQUEST).build();
        }

        entity = app.merge(getParameterMap(uriInfo, persistenceUnit), entity);
        return Response.ok(new StreamingOutputMarshaller(app, entity, hh.getAcceptableMediaTypes())).build();
    }
    
    @POST
    @Path("{type}/{key}/{attribute}")
    public Response setOrAddAttribute(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("key") String key, @PathParam("attribute") String attribute,
            @Context HttpHeaders hh, @Context UriInfo ui, InputStream in) {
        return setOrAddAttribute(persistenceUnit, type, key, attribute, hh, ui, ui.getBaseUri(), in);
    }

    protected Response setOrAddAttribute(String persistenceUnit, String type, String key, String attribute, HttpHeaders hh, UriInfo ui, URI baseURI, InputStream in) {
        PersistenceContext app = getPersistenceFactory().get(persistenceUnit, baseURI, null);
        if (app == null || app.getClass(type) == null) {
            if (app == null) {
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
            }
            return Response.status(Status.NOT_FOUND).build();
        }

        Map<String, String> discriminators = getParameterMap(ui, persistenceUnit);
        Object id = IdHelper.buildId(app, type, key, discriminators);

        Object entity = null;
        String partner = (String) getParameterMap(ui, attribute).get(RELATIONSHIP_PARTNER);
        try {
            ClassDescriptor descriptor = app.getDescriptor(type);
            DatabaseMapping mapping = (DatabaseMapping) descriptor.getMappingForAttributeName(attribute);
            if (!mapping.isForeignReferenceMapping()) {
                JPARSLogger.fine("jpars_could_find_appropriate_mapping_for_update", new Object[] { attribute, type, key, persistenceUnit });
                return Response.status(Status.NOT_FOUND).build();
            }
            entity = app.unmarshalEntity(((ForeignReferenceMapping) mapping).getReferenceDescriptor().getAlias(), mediaType(hh.getAcceptableMediaTypes()), in);
        } catch (JAXBException e) {
            JPARSLogger.fine("exception_while_unmarhalling_entity", new Object[] { type, persistenceUnit, e.toString() });
            return Response.status(Status.BAD_REQUEST).build();
        }

        Object result = app.updateOrAddAttribute(getParameterMap(ui, persistenceUnit), type, id, getHintMap(ui), attribute, entity, partner);

        if (result == null) {
            JPARSLogger.fine("jpars_could_not_update_attribute", new Object[] { attribute, type, key, persistenceUnit });
            return Response.status(Status.NOT_FOUND).build();
        } else {
            return Response.ok(new StreamingOutputMarshaller(app, result, hh.getAcceptableMediaTypes())).build();
        }
    }
    
    @DELETE
    @Path("{type}/{key}/{attribute}")
    public Response removeAttribute(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("key") String key, @PathParam("attribute") String attribute,
            @Context HttpHeaders hh, @Context UriInfo ui, InputStream in) {
        return removeAttribute(persistenceUnit, type, key, attribute, hh, ui, ui.getBaseUri(), in);
    }

    protected Response removeAttribute(String persistenceUnit, String type, String key, String attribute, HttpHeaders hh, UriInfo ui, URI baseURI, InputStream in) {
        PersistenceContext app = getPersistenceFactory().get(persistenceUnit, baseURI, null);
        if (app == null || app.getClass(type) == null) {
            if (app == null) {
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
            }
            return Response.status(Status.NOT_FOUND).build();
        }

        Map<String, String> discriminators = getParameterMap(ui, persistenceUnit);
        Object id = IdHelper.buildId(app, type, key, discriminators);

        Object entity = null;
        String partner = (String) getParameterMap(ui, attribute).get(RELATIONSHIP_PARTNER);
        try {
            ClassDescriptor descriptor = app.getDescriptor(type);
            DatabaseMapping mapping = (DatabaseMapping) descriptor.getMappingForAttributeName(attribute);
            if (!mapping.isForeignReferenceMapping()) {
                JPARSLogger.fine("jpars_could_find_appropriate_mapping_for_update", new Object[] { attribute, type, key, persistenceUnit });
                return Response.status(Status.NOT_FOUND).build();
            }
            entity = app.unmarshalEntity(((ForeignReferenceMapping) mapping).getReferenceDescriptor().getAlias(), mediaType(hh.getAcceptableMediaTypes()), in);
        } catch (JAXBException e) {
            JPARSLogger.fine("exception_while_unmarhalling_entity", new Object[] { type, persistenceUnit, e.toString() });
            return Response.status(Status.BAD_REQUEST).build();
        }

        Object result = app.removeAttribute(getParameterMap(ui, persistenceUnit), type, id, getHintMap(ui), attribute, entity, partner);

        if (result == null) {
            JPARSLogger.fine("jpars_could_not_update_attribute", new Object[] { attribute, type, key, persistenceUnit });
            return Response.status(Status.NOT_FOUND).build();
        } else {
            return Response.ok(new StreamingOutputMarshaller(app, result, hh.getAcceptableMediaTypes())).build();
        }
    }
    
    @DELETE
    @Path("{type}/{key}")
    public Response delete(@PathParam("context") String persistenceUnit, @PathParam("type") String type, @PathParam("key") String key, @Context UriInfo ui) {
        return delete(persistenceUnit, type, key, ui, ui.getBaseUri());
    }
    
    protected Response delete(String persistenceUnit, String type, String key, UriInfo ui, URI baseURI) {
        PersistenceContext app = getPersistenceFactory().get(persistenceUnit, baseURI, null);
        if (app == null || app.getClass(type) == null) {
            if (app == null) {
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
            }
            return Response.status(Status.NOT_FOUND).build();
        }
        Map<String, String> discriminators = getParameterMap(ui, persistenceUnit);
        Object id = IdHelper.buildId(app, type, key, discriminators);
        app.delete(discriminators, type, id);
        return Response.ok().build();
    }
}
