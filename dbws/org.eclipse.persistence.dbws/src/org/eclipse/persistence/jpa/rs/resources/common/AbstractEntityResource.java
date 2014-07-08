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
 *      Dmitry Kornilov - pagination related changes
 ******************************************************************************/

package org.eclipse.persistence.jpa.rs.resources.common;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.QueryParameters;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.features.FeatureResponseBuilder;
import org.eclipse.persistence.jpa.rs.features.FeatureSet;
import org.eclipse.persistence.jpa.rs.features.FeatureSet.Feature;
import org.eclipse.persistence.jpa.rs.features.paging.PageableFieldValidator;
import org.eclipse.persistence.jpa.rs.util.IdHelper;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DatabaseMapping.WriteType;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.sessions.DatabaseSession;

import javax.persistence.EntityManager;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller.mediaType;

/**
 * Base class for entity resource.
 *
 * @author gonural
 */
public abstract class AbstractEntityResource extends AbstractResource {
    private static final String CLASS_NAME = AbstractEntityResource.class.getName();

    protected Response findAttributeInternal(String version, String persistenceUnit, String type, String id, String attribute, HttpHeaders headers, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "findAttributeInternal", new Object[] { "GET", version, persistenceUnit, type, id, attribute, uriInfo.getRequestUri().toASCIIString() });

        EntityManager em = null;
        try {
            PersistenceContext context = getPersistenceContext(persistenceUnit, type, uriInfo.getBaseUri(), version, null);
            Object entityId = IdHelper.buildId(context, type, id);
            em = context.getEmf().createEntityManager(getMatrixParameters(uriInfo, persistenceUnit));

            Object entity = em.find(context.getClass(type), entityId, getQueryParameters(uriInfo));
            DatabaseSession serverSession = context.getServerSession();
            ClassDescriptor descriptor = serverSession.getClassDescriptor(context.getClass(type));
            if (descriptor == null) {
                throw JPARSException.classOrClassDescriptorCouldNotBeFoundForEntity(type, persistenceUnit);
            }

            DatabaseMapping attributeMapping = descriptor.getMappingForAttributeName(attribute);
            if ((attributeMapping == null) || (entity == null)) {
                throw JPARSException.databaseMappingCouldNotBeFoundForEntityAttribute(attribute, type, id, persistenceUnit);
            }

            if (!attributeMapping.isCollectionMapping()) {
                Object result = attributeMapping.getRealAttributeValueFromAttribute(attributeMapping.getAttributeValueFromObject(entity), entity, (AbstractSession) serverSession);
                if (result == null) {
                    JPARSLogger.error("jpars_could_not_find_entity_for_attribute", new Object[] { attribute, type, id, persistenceUnit });
                    throw JPARSException.attributeCouldNotBeFoundForEntity(attribute, type, id, persistenceUnit);
                }
                return findAttributeResponse(context, attribute, type, id, persistenceUnit, result, getQueryParameters(uriInfo), headers, uriInfo, context.getSupportedFeatureSet().getResponseBuilder(Feature.NO_PAGING));
            }

            ReadQuery query = (ReadQuery) ((((ForeignReferenceMapping) attributeMapping).getSelectionQuery()).clone());
            if (query == null) {
                throw JPARSException.selectionQueryForAttributeCouldNotBeFoundForEntity(attribute, type, id, persistenceUnit);
            }

            FeatureSet featureSet = context.getSupportedFeatureSet();
            AbstractSession clientSession = context.getClientSession(em);
            if (featureSet.isSupported(Feature.PAGING)) {
                PageableFieldValidator validator = new PageableFieldValidator(entity.getClass(), attribute, uriInfo);
                if (validator.isFeatureApplicable()) {
                    query.setMaxRows(validator.getLimit());
                    query.setFirstResult(validator.getOffset());

                    // We need to add limit and offset to query parameters because request builder reads it from there
                    Map<String, Object> queryParams = getQueryParameters(uriInfo);
                    queryParams.put(QueryParameters.JPARS_PAGING_LIMIT, String.valueOf(validator.getLimit()));
                    queryParams.put(QueryParameters.JPARS_PAGING_OFFSET, String.valueOf(validator.getOffset()));

                    // check orderBy, and generate a warning if there is none
                    checkOrderBy(query);

                    Object result = clientSession.executeQuery(query, descriptor.getObjectBuilder().buildRow(entity, clientSession, WriteType.INSERT));
                    return findAttributeResponse(context, attribute, type, id, persistenceUnit, result, queryParams, headers, uriInfo, context.getSupportedFeatureSet().getResponseBuilder(Feature.PAGING));
                }
            }
            Object result = clientSession.executeQuery(query, descriptor.getObjectBuilder().buildRow(entity, clientSession, WriteType.INSERT));
            return findAttributeResponse(context, attribute, type, id, persistenceUnit, result, getQueryParameters(uriInfo), headers, uriInfo, context.getSupportedFeatureSet().getResponseBuilder(Feature.NO_PAGING));
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        } finally {
            if (em != null) {
                if (em.isOpen()) {
                    em.close();
                }
            }
        }
    }

    protected Response findInternal(String version, String persistenceUnit, String type, String id, HttpHeaders headers, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "findInternal", new Object[] { "GET", version, persistenceUnit, type, id, uriInfo.getRequestUri().toASCIIString() });

        try {
            PersistenceContext context = getPersistenceContext(persistenceUnit, type, uriInfo.getBaseUri(), version, null);
            Map<String, String> discriminators = getMatrixParameters(uriInfo, persistenceUnit);
            Object entityId = IdHelper.buildId(context, type, id);
            Object entity = context.find(discriminators, type, entityId, getQueryParameters(uriInfo));
            if (entity == null) {
                JPARSLogger.error("jpars_could_not_find_entity_for_key", new Object[] { type, id, persistenceUnit });
                throw JPARSException.entityNotFound(type, id, persistenceUnit);
            }
            return Response.ok(new StreamingOutputMarshaller(context, singleEntityResponse(context, entity, uriInfo), headers.getAcceptableMediaTypes())).build();
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    @SuppressWarnings("rawtypes")
    protected Response createInternal(String version, String persistenceUnit, String type, HttpHeaders headers, UriInfo uriInfo, InputStream in) {
        JPARSLogger.entering(CLASS_NAME, "createInternal", new Object[] { "PUT", headers.getMediaType(), version, persistenceUnit, type, uriInfo.getRequestUri().toASCIIString() });
        try {
            PersistenceContext context = getPersistenceContext(persistenceUnit, type, uriInfo.getBaseUri(), version, null);
            ClassDescriptor descriptor = context.getDescriptor(type);
            if (descriptor == null) {
                JPARSLogger.error("jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
                throw JPARSException.classOrClassDescriptorCouldNotBeFoundForEntity(type, persistenceUnit);
            }

            Object entity = context.unmarshalEntity(type, mediaType(headers.getAcceptableMediaTypes()), in);

            // maintain idempotence on PUT by disallowing sequencing
            AbstractDirectMapping sequenceMapping = descriptor.getObjectBuilder().getSequenceMapping();
            if (sequenceMapping != null) {
                Object value = sequenceMapping.getAttributeAccessor().getAttributeValueFromObject(entity);

                if (descriptor.getObjectBuilder().isPrimaryKeyComponentInvalid(value, descriptor.getPrimaryKeyFields().indexOf(descriptor.getSequenceNumberField()))
                        || descriptor.getSequence().shouldAlwaysOverrideExistingValue()) {
                    JPARSLogger.error("jpars_put_not_idempotent", new Object[] { type, persistenceUnit });
                    throw JPARSException.entityIsNotIdempotent(type, persistenceUnit);
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
                                                Object obj = holder.getValue();
                                                if (obj != null) {
                                                    JPARSLogger.error("jpars_put_not_idempotent", new Object[] { type, persistenceUnit });
                                                    throw JPARSException.entityIsNotIdempotent(type, persistenceUnit);
                                                }
                                            } else if (value instanceof Collection) {
                                                if (!(((Collection) value).isEmpty())) {
                                                    JPARSLogger.error("jpars_put_not_idempotent", new Object[] { type, persistenceUnit });
                                                    throw JPARSException.entityIsNotIdempotent(type, persistenceUnit);
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
            return rb.entity(new StreamingOutputMarshaller(context, singleEntityResponse(context, entity, uriInfo), headers.getAcceptableMediaTypes())).build();
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    protected Response updateInternal(String version, String persistenceUnit, String type, HttpHeaders headers, UriInfo uriInfo, InputStream in) {
        JPARSLogger.entering(CLASS_NAME, "updateInternal", new Object[] { "POST", headers.getMediaType(), version, persistenceUnit, type, uriInfo.getRequestUri().toASCIIString() });
        try {
            PersistenceContext context = getPersistenceContext(persistenceUnit, type, uriInfo.getBaseUri(), version, null);
            Object entity = context.unmarshalEntity(type, mediaType(headers.getAcceptableMediaTypes()), in);
            entity = context.merge(getMatrixParameters(uriInfo, persistenceUnit), entity);
            return Response.ok(new StreamingOutputMarshaller(context, singleEntityResponse(context, entity, uriInfo), headers.getAcceptableMediaTypes())).build();
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    protected Response setOrAddAttributeInternal(String version, String persistenceUnit, String type, String id, String attribute, HttpHeaders headers, UriInfo uriInfo, InputStream in) {
        JPARSLogger.entering(CLASS_NAME, "setOrAddAttributeInternal", new Object[] { "POST", headers.getMediaType(), version, persistenceUnit, type, id, attribute, uriInfo.getRequestUri().toASCIIString() });
        try {
            PersistenceContext context = getPersistenceContext(persistenceUnit, type, uriInfo.getBaseUri(), version, null);
            Object entityId = IdHelper.buildId(context, type, id);
            String partner = getRelationshipPartner(getMatrixParameters(uriInfo, attribute), getQueryParameters(uriInfo));
            ClassDescriptor descriptor = context.getDescriptor(type);
            DatabaseMapping mapping = descriptor.getMappingForAttributeName(attribute);
            if (!mapping.isForeignReferenceMapping()) {
                JPARSLogger.error("jpars_could_not_find_appropriate_mapping_for_update", new Object[] { attribute, type, id, persistenceUnit });
                throw JPARSException.databaseMappingCouldNotBeFoundForEntityAttribute(attribute, type, id, persistenceUnit);
            }
            Object entity = context.unmarshalEntity(mapping.getReferenceDescriptor().getAlias(), mediaType(headers.getAcceptableMediaTypes()), in);
            Object result = context.updateOrAddAttribute(getMatrixParameters(uriInfo, persistenceUnit), type, entityId, getQueryParameters(uriInfo), attribute, entity, partner);
            if (result == null) {
                JPARSLogger.error("jpars_could_not_update_attribute", new Object[] { attribute, type, id, persistenceUnit });
                JPARSException.attributeCouldNotBeUpdated(attribute, type, id, persistenceUnit);
            }
            return Response.ok(new StreamingOutputMarshaller(context, singleEntityResponse(context, result, uriInfo), headers.getAcceptableMediaTypes())).build();
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    protected Response removeAttributeInternal(String version, String persistenceUnit, String type, String id, String attribute, HttpHeaders headers, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "removeAttributeInternal", new Object[] { "DELETE", headers.getMediaType(), version, persistenceUnit, type, id, attribute, uriInfo.getRequestUri().toASCIIString() });
        try {
            String listItemId = null;
            Map<String, String> matrixParams = getMatrixParameters(uriInfo, attribute);
            Map<String, Object> queryParams = getQueryParameters(uriInfo);

            if ((queryParams != null) && (!queryParams.isEmpty())) {
                listItemId = (String) queryParams.get(QueryParameters.JPARS_LIST_ITEM_ID);
            }

            if ((attribute == null) && (listItemId == null)) {
                JPARSException.invalidRemoveAttributeRequest(null, type, id, persistenceUnit);
            }

            String partner = getRelationshipPartner(matrixParams, queryParams);
            PersistenceContext context = getPersistenceContext(persistenceUnit, type, uriInfo.getBaseUri(), version, null);
            Object entityId = IdHelper.buildId(context, type, id);
            ClassDescriptor descriptor = context.getDescriptor(type);
            DatabaseMapping mapping = descriptor.getMappingForAttributeName(attribute);
            if (!mapping.isForeignReferenceMapping()) {
                JPARSLogger.error("jpars_could_not_find_appropriate_mapping_for_update", new Object[] { attribute, type, id, persistenceUnit });
                throw JPARSException.databaseMappingCouldNotBeFoundForEntityAttribute(attribute, type, id, persistenceUnit);
            }

            Map<String, String> discriminators = getMatrixParameters(uriInfo, persistenceUnit);
            Object entity = context.find(discriminators, type, entityId, getQueryParameters(uriInfo));
            Object result = context.removeAttribute(getMatrixParameters(uriInfo, persistenceUnit), type, entityId, attribute, listItemId, entity, partner);

            if (result == null) {
                JPARSLogger.error("jpars_could_not_update_attribute", new Object[] { attribute, type, id, persistenceUnit });
                throw JPARSException.attributeCouldNotBeUpdated(attribute, type, id, persistenceUnit);
            } else {
                return Response.ok(new StreamingOutputMarshaller(context, singleEntityResponse(context, result, uriInfo), headers.getAcceptableMediaTypes())).build();
            }
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    protected Response deleteInternal(String version, String persistenceUnit, String type, String id, HttpHeaders headers, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "deleteInternal", new Object[] { "DELETE", headers.getMediaType(), version, persistenceUnit, type, id, uriInfo.getRequestUri().toASCIIString() });
        try {
            PersistenceContext context = getPersistenceContext(persistenceUnit, type, uriInfo.getBaseUri(), version, null);
            Map<String, String> discriminators = getMatrixParameters(uriInfo, persistenceUnit);
            Object entityId = IdHelper.buildId(context, type, id);
            context.delete(discriminators, type, entityId);
            return Response.ok().build();
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    private Response findAttributeResponse(PersistenceContext context, String attribute, String entityType, String id, String persistenceUnit, Object queryResults, Map<String, Object> queryParams, HttpHeaders headers, UriInfo uriInfo, FeatureResponseBuilder responseBuilder) {
        if (queryResults != null) {
            Object results = responseBuilder.buildAttributeResponse(context, queryParams, attribute, queryResults, uriInfo);
            if (results != null) {
                return Response.ok(new StreamingOutputMarshaller(context, results, headers.getAcceptableMediaTypes())).build();
            } else {
                // something is wrong with the descriptors
                throw JPARSException.responseCouldNotBeBuiltForFindAttributeRequest(attribute, entityType, id, persistenceUnit);
            }
        }
        return Response.ok(new StreamingOutputMarshaller(context, null, headers.getAcceptableMediaTypes())).build();
    }

    private void checkOrderBy(ReadQuery query) {
        if (query.isReadAllQuery()) {
            ReadAllQuery readAllQuery = (ReadAllQuery) query;
            List<Expression> orderBy = readAllQuery.getOrderByExpressions();
            if ((orderBy == null) || (orderBy.isEmpty())) {
                JPARSLogger.warning("no_orderby_clause_for_paging", new Object[] { query.toString() });
            }
        }
    }

    private Object singleEntityResponse(PersistenceContext context, Object entity, UriInfo uriInfo) {
        FeatureSet featureSet = context.getSupportedFeatureSet();
        FeatureResponseBuilder responseBuilder = featureSet.getResponseBuilder(Feature.NO_PAGING);
        return responseBuilder.buildSingleEntityResponse(context, getQueryParameters(uriInfo), entity, uriInfo);
    }
}