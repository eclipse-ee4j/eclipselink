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

package org.eclipse.persistence.jpa.rs.resources.common;

import static org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller.mediaType;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.QueryParameters;
import org.eclipse.persistence.jpa.rs.features.FeatureRequestValidator;
import org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilder;
import org.eclipse.persistence.jpa.rs.features.FeatureSet;
import org.eclipse.persistence.jpa.rs.features.FeatureSet.Feature;
import org.eclipse.persistence.jpa.rs.features.clientinitiated.paging.PagingRequestValidator;
import org.eclipse.persistence.jpa.rs.util.IdHelper;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * @author gonural
 *
 */
public abstract class AbstractEntityResource extends AbstractResource {
    protected Response findAttribute(String version, String persistenceUnit, String type, String key, String attribute, HttpHeaders headers, UriInfo uriInfo, URI baseURI) {
        PersistenceContext context = getPersistenceContext(persistenceUnit, baseURI, version, null);
        if (context == null || context.getClass(type) == null) {
            if (context == null) {
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
            }
            return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }

        Object id = IdHelper.buildId(context, type, key);
        EntityManager em = context.getEmf().createEntityManager(getMatrixParameters(uriInfo, persistenceUnit));

        Object entity = null;

        try {
            entity = em.find(context.getClass(type), id, getQueryParameters(uriInfo));

            DatabaseSession serverSession = context.getServerSession();

            ClassDescriptor descriptor = serverSession.getClassDescriptor(context.getClass(type));
            if (descriptor == null) {
                return Response.status(Status.BAD_REQUEST).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
            }

            DatabaseMapping mapping = descriptor.getMappingForAttributeName(attribute);
            if ((mapping == null) || (entity == null)) {
                return Response.status(Status.BAD_REQUEST).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
            }

            Object result = null;

            if (!mapping.isCollectionMapping()) {
                result = mapping.getRealAttributeValueFromAttribute(mapping.getAttributeValueFromObject(entity), entity, (AbstractSession) serverSession);
                if (result == null) {
                    JPARSLogger.fine("jpars_could_not_entity_for_attribute", new Object[] { attribute, type, key, persistenceUnit });
                    return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
                }
                return response(context, attribute, result, headers, uriInfo, context.getSupportedFeatureSet().getResponseBuilder(Feature.NO_PAGING));
            }

            ReadQuery query = (ReadQuery) ((((ForeignReferenceMapping) mapping).getSelectionQuery()).clone());

            if (query == null) {
                return Response.status(Status.BAD_REQUEST).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
            }

            FeatureSet featureSet = context.getSupportedFeatureSet();
            AbstractSession clientSession = context.getClientSession(em);

            if (featureSet.isSupported(Feature.PAGING)) {
                FeatureRequestValidator requestValidator = featureSet.getRequestValidator(Feature.PAGING);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(PagingRequestValidator.DB_QUERY, query);
                if (requestValidator.isRequested(uriInfo, null)) {
                    if (!requestValidator.isRequestValid(uriInfo, map)) {
                        return Response.status(Status.BAD_REQUEST).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
                    }
                    result = clientSession.executeQuery(query, descriptor.getObjectBuilder().buildRow(entity, clientSession, WriteType.INSERT));
                    return response(context, attribute, result, headers, uriInfo, context.getSupportedFeatureSet().getResponseBuilder(Feature.PAGING));
                }
            }
            result = clientSession.executeQuery(query, descriptor.getObjectBuilder().buildRow(entity, clientSession, WriteType.INSERT));
            return response(context, attribute, result, headers, uriInfo, context.getSupportedFeatureSet().getResponseBuilder(Feature.NO_PAGING));
        } finally {
            em.close();
        }
    }

    private Response response(PersistenceContext context, String attribute, Object queryResults, HttpHeaders headers, UriInfo uriInfo, FeatureResponseBuilder responseBuilder) {
        Map<String, Object> queryParams = getQueryParameters(uriInfo);
        if (queryResults != null) {
            Object results = responseBuilder.buildCollectionAttributeResponse(context, queryParams, attribute, queryResults, uriInfo);
            if (results != null) {
                return Response.ok(new StreamingOutputMarshaller(context, results, headers.getAcceptableMediaTypes())).build();
            } else {
                // something is wrong with the descriptors
                return Response.status(Status.INTERNAL_SERVER_ERROR).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
            }
        }
        return Response.ok(new StreamingOutputMarshaller(context, queryResults, headers.getAcceptableMediaTypes())).build();
    }

    protected Response find(String version, String persistenceUnit, String type, String key, HttpHeaders headers, UriInfo uriInfo, URI baseURI) {
        PersistenceContext context = getPersistenceContext(persistenceUnit, baseURI, version, null);
        if (context == null || context.getClass(type) == null) {
            if (context == null) {
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
            }
            return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }
        Map<String, String> discriminators = getMatrixParameters(uriInfo, persistenceUnit);

        Object id = IdHelper.buildId(context, type, key);

        Object entity = context.find(discriminators, type, id, getQueryParameters(uriInfo));

        if (entity == null) {
            JPARSLogger.fine("jpars_could_not_entity_for_key", new Object[] { type, key, persistenceUnit });
            return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        } else {
            return Response.ok(new StreamingOutputMarshaller(context, entity, headers.getAcceptableMediaTypes())).build();
        }
    }

    @SuppressWarnings("rawtypes")
    protected Response create(String version, String persistenceUnit, String type, HttpHeaders headers, UriInfo uriInfo, URI baseURI, InputStream in) throws JAXBException {
        PersistenceContext context = getPersistenceContext(persistenceUnit, baseURI, version, null);
        if (context == null) {
            JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }
        ClassDescriptor descriptor = context.getDescriptor(type);
        if (descriptor == null) {
            JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
            return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }
        Object entity = null;
        try {
            entity = context.unmarshalEntity(type, mediaType(headers.getAcceptableMediaTypes()), in);
        } catch (JAXBException e) {
            JPARSLogger.fine("exception_while_unmarhalling_entity", new Object[] { type, persistenceUnit, e.toString() });
            return Response.status(Status.BAD_REQUEST).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }

        // maintain idempotence on PUT by disallowing sequencing
        AbstractDirectMapping sequenceMapping = descriptor.getObjectBuilder().getSequenceMapping();
        if (sequenceMapping != null) {
            Object value = sequenceMapping.getAttributeAccessor().getAttributeValueFromObject(entity);

            if (descriptor.getObjectBuilder().isPrimaryKeyComponentInvalid(value, descriptor.getPrimaryKeyFields().indexOf(descriptor.getSequenceNumberField()))
                    || descriptor.getSequence().shouldAlwaysOverrideExistingValue()) {
                JPARSLogger.fine("jpars_put_not_idempotent", new Object[] { type, persistenceUnit });
                return Response.status(Status.BAD_REQUEST).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
            }
        }

        // maintain idempotence on PUT by disallowing sequencing in relationships
        List<DatabaseMapping> mappings = descriptor.getMappings();
        if ((mappings != null) && (!mappings.isEmpty())) {
            for (DatabaseMapping mapping : mappings) {
                if (mapping instanceof ForeignReferenceMapping) {
                    ForeignReferenceMapping fkMapping = (ForeignReferenceMapping) mapping;
                    if ((fkMapping.isCascadePersist()) || (fkMapping.isCascadeMerge())) {
                        ClassDescriptor referenceDescriptor = fkMapping.getReferenceDescriptor();
                        if (referenceDescriptor != null) {
                            if (referenceDescriptor instanceof RelationalDescriptor) {
                                RelationalDescriptor relDesc = (RelationalDescriptor) referenceDescriptor;
                                AbstractDirectMapping relSequenceMapping = relDesc.getObjectBuilder().getSequenceMapping();
                                if (relSequenceMapping != null) {
                                    Object value = mapping.getAttributeAccessor().getAttributeValueFromObject(entity);
                                    if (value != null) {
                                        if (value instanceof ValueHolder) {
                                            ValueHolder holder = (ValueHolder) value;
                                            if (holder != null) {
                                                Object obj = holder.getValue();
                                                if (obj != null) {
                                                    JPARSLogger.fine("jpars_put_not_idempotent", new Object[] { type, persistenceUnit });
                                                    return Response.status(Status.BAD_REQUEST).build();
                                                }
                                            }
                                        } else if (value instanceof Collection) {
                                            if (!(((Collection) value).isEmpty())) {
                                                JPARSLogger.fine("jpars_put_not_idempotent", new Object[] { type, persistenceUnit });
                                                return Response.status(Status.BAD_REQUEST).build();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // No sequencing in relationships, we can create the object now...
        context.create(getMatrixParameters(uriInfo, persistenceUnit), entity);
        ResponseBuilder rb = Response.status(Status.CREATED);
        rb.entity(new StreamingOutputMarshaller(context, entity, headers.getAcceptableMediaTypes()));
        return rb.build();
    }

    protected Response update(String version, String persistenceUnit, String type, HttpHeaders headers, UriInfo uriInfo, URI baseURI, InputStream in) {
        PersistenceContext context = getPersistenceContext(persistenceUnit, baseURI, version, null);
        if (context == null || context.getClass(type) == null) {
            if (context == null) {
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
            }
            return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }
        Object entity = null;
        try {
            entity = context.unmarshalEntity(type, mediaType(headers.getAcceptableMediaTypes()), in);
        } catch (JAXBException e) {
            JPARSLogger.fine("exception_while_unmarhalling_entity", new Object[] { type, persistenceUnit, e.toString() });
            return Response.status(Status.BAD_REQUEST).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }

        entity = context.merge(getMatrixParameters(uriInfo, persistenceUnit), entity);
        return Response.ok(new StreamingOutputMarshaller(context, entity, headers.getAcceptableMediaTypes())).build();
    }

    protected Response setOrAddAttribute(String version, String persistenceUnit, String type, String key, String attribute, HttpHeaders headers, UriInfo uriInfo, URI baseURI, InputStream in) {
        PersistenceContext context = getPersistenceContext(persistenceUnit, baseURI, version, null);
        if (context == null || context.getClass(type) == null) {
            if (context == null) {
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
            }
            return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }

        Object id = IdHelper.buildId(context, type, key);

        Object entity = null;
        String partner = getRelationshipPartner(getMatrixParameters(uriInfo, attribute), getQueryParameters(uriInfo));
        try {
            ClassDescriptor descriptor = context.getDescriptor(type);
            DatabaseMapping mapping = (DatabaseMapping) descriptor.getMappingForAttributeName(attribute);
            if (!mapping.isForeignReferenceMapping()) {
                JPARSLogger.fine("jpars_could_find_appropriate_mapping_for_update", new Object[] { attribute, type, key, persistenceUnit });
                return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
            }
            entity = context.unmarshalEntity(((ForeignReferenceMapping) mapping).getReferenceDescriptor().getAlias(), mediaType(headers.getAcceptableMediaTypes()), in);
        } catch (JAXBException e) {
            JPARSLogger.fine("exception_while_unmarhalling_entity", new Object[] { type, persistenceUnit, e.toString() });
            return Response.status(Status.BAD_REQUEST).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }

        Object result = context.updateOrAddAttribute(getMatrixParameters(uriInfo, persistenceUnit), type, id, getQueryParameters(uriInfo), attribute, entity, partner);

        if (result == null) {
            JPARSLogger.fine("jpars_could_not_update_attribute", new Object[] { attribute, type, key, persistenceUnit });
            return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        } else {
            return Response.ok(new StreamingOutputMarshaller(context, result, headers.getAcceptableMediaTypes())).build();
        }
    }

    protected Response removeAttributeInternal(String version, String persistenceUnit, String type, String key, String attribute, HttpHeaders headers, UriInfo uriInfo) {
        String listItemId = null;
        Map<String, String> matrixParams = getMatrixParameters(uriInfo, attribute);
        Map<String, Object> queryParams = getQueryParameters(uriInfo);

        if ((queryParams != null) && (!queryParams.isEmpty())) {
            listItemId = (String) queryParams.get(QueryParameters.JPARS_LIST_ITEM_ID);
        }

        String partner = getRelationshipPartner(matrixParams, queryParams);

        PersistenceContext context = getPersistenceContext(persistenceUnit, uriInfo.getBaseUri(), version, null);
        if (context == null || context.getClass(type) == null) {
            if (context == null) {
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
            }
            return Response.status(Status.BAD_REQUEST).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }

        if ((attribute == null) && (listItemId == null)) {
            return Response.status(Status.BAD_REQUEST).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }

        Object id = IdHelper.buildId(context, type, key);

        ClassDescriptor descriptor = context.getDescriptor(type);
        DatabaseMapping mapping = (DatabaseMapping) descriptor.getMappingForAttributeName(attribute);
        if (!mapping.isForeignReferenceMapping()) {
            JPARSLogger.fine("jpars_could_find_appropriate_mapping_for_update", new Object[] { attribute, type, key, persistenceUnit });
            return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }

        Map<String, String> discriminators = getMatrixParameters(uriInfo, persistenceUnit);
        Object entity = context.find(discriminators, type, id, getQueryParameters(uriInfo));
        Object result = context.removeAttribute(getMatrixParameters(uriInfo, persistenceUnit), type, id, attribute, listItemId, entity, partner);

        if (result == null) {
            JPARSLogger.fine("jpars_could_not_update_attribute", new Object[] { attribute, type, key, persistenceUnit });
            return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        } else {
            return Response.ok(new StreamingOutputMarshaller(context, result, headers.getAcceptableMediaTypes())).build();
        }
    }

    protected Response delete(String version, String persistenceUnit, String type, String key, UriInfo uriInfo, HttpHeaders headers, URI baseURI) {
        PersistenceContext context = getPersistenceContext(persistenceUnit, baseURI, version, null);
        if (context == null || context.getClass(type) == null) {
            if (context == null) {
                JPARSLogger.fine("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            } else {
                JPARSLogger.fine("jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
            }
            return Response.status(Status.NOT_FOUND).type(StreamingOutputMarshaller.getResponseMediaType(headers)).build();
        }

        Map<String, String> discriminators = getMatrixParameters(uriInfo, persistenceUnit);
        Object id = IdHelper.buildId(context, type, key);
        context.delete(discriminators, type, id);
        return Response.ok().build();
    }
}
