/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//      gonural - Initial implementation
//      2014-09-01-2.6.0 Dmitry Kornilov
//        - JPARS v2.0 related changes
package org.eclipse.persistence.jpa.rs.resources.common;

import static org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller.mediaType;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

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
import org.eclipse.persistence.jpa.rs.features.ServiceVersion;
import org.eclipse.persistence.jpa.rs.features.fieldsfiltering.FieldsFilter;
import org.eclipse.persistence.jpa.rs.features.fieldsfiltering.FieldsFilteringValidator;
import org.eclipse.persistence.jpa.rs.features.paging.PageableFieldValidator;
import org.eclipse.persistence.jpa.rs.util.HrefHelper;
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
                    JPARSLogger.error(context.getSessionLog(), "jpars_could_not_find_entity_for_attribute", new Object[] { attribute, type, id, persistenceUnit });
                    throw JPARSException.attributeCouldNotBeFoundForEntity(attribute, type, id, persistenceUnit);
                }
                final FeatureResponseBuilder responseBuilder = context.getSupportedFeatureSet().getResponseBuilder(Feature.NO_PAGING);
                return findAttributeResponse(context, attribute, type, id, persistenceUnit, result, getQueryParameters(uriInfo), headers, uriInfo, responseBuilder, null);
            }

            ReadQuery query = (ReadQuery) ((((ForeignReferenceMapping) attributeMapping).getSelectionQuery()).clone());
            if (query == null) {
                throw JPARSException.selectionQueryForAttributeCouldNotBeFoundForEntity(attribute, type, id, persistenceUnit);
            }

            final FeatureSet featureSet = context.getSupportedFeatureSet();
            final AbstractSession clientSession = context.getClientSession(em);

            // Fields filtering
            FieldsFilter fieldsFilter = null;
            if (context.getSupportedFeatureSet().isSupported(Feature.FIELDS_FILTERING)) {
                final FieldsFilteringValidator fieldsFilteringValidator = new FieldsFilteringValidator(uriInfo);
                if (fieldsFilteringValidator.isFeatureApplicable()) {
                    fieldsFilter = fieldsFilteringValidator.getFilter();
                }
            }

            // Pagination
            if (featureSet.isSupported(Feature.PAGING)) {
                final PageableFieldValidator validator = new PageableFieldValidator(entity.getClass(), attribute, uriInfo);
                if (validator.isFeatureApplicable()) {
                    // Adding extra one to detect are there more rows or not. It will be removed later
                    // on in response processor.
                    query.setMaxRows(validator.getLimit() + validator.getOffset() + 1);
                    query.setFirstResult(validator.getOffset());

                    // We need to add limit and offset to query parameters because request builder reads it from there
                    final Map<String, Object> queryParams = getQueryParameters(uriInfo);
                    queryParams.put(QueryParameters.JPARS_PAGING_LIMIT, String.valueOf(validator.getLimit()));
                    queryParams.put(QueryParameters.JPARS_PAGING_OFFSET, String.valueOf(validator.getOffset()));

                    // check orderBy, and generate a warning if there is none
                    checkOrderBy(context, query);

                    final Object result = clientSession.executeQuery(query, descriptor.getObjectBuilder().buildRow(entity, clientSession, WriteType.INSERT));
                    final FeatureResponseBuilder responseBuilder = context.getSupportedFeatureSet().getResponseBuilder(Feature.PAGING);
                    return findAttributeResponse(context, attribute, type, id, persistenceUnit, result, queryParams, headers, uriInfo, responseBuilder, fieldsFilter);
                }
            }

            final Object result = clientSession.executeQuery(query, descriptor.getObjectBuilder().buildRow(entity, clientSession, WriteType.INSERT));
            final FeatureResponseBuilder responseBuilder = context.getSupportedFeatureSet().getResponseBuilder(Feature.NO_PAGING);
            return findAttributeResponse(context, attribute, type, id, persistenceUnit, result, getQueryParameters(uriInfo), headers, uriInfo, responseBuilder, fieldsFilter);
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
            final PersistenceContext context = getPersistenceContext(persistenceUnit, type, uriInfo.getBaseUri(), version, null);
            final Map<String, String> discriminators = getMatrixParameters(uriInfo, persistenceUnit);
            final Object entityId = IdHelper.buildId(context, type, id);
            final Object entity = context.find(discriminators, type, entityId, getQueryParameters(uriInfo));
            if (entity == null) {
                JPARSLogger.error(context.getSessionLog(), "jpars_could_not_find_entity_for_key", new Object[] { type, id, persistenceUnit });
                throw JPARSException.entityNotFound(type, id, persistenceUnit);
            }

            // Fields filtering
            if (context.getSupportedFeatureSet().isSupported(Feature.FIELDS_FILTERING)) {
                final FieldsFilteringValidator fieldsFilteringValidator = new FieldsFilteringValidator(uriInfo);
                if (fieldsFilteringValidator.isFeatureApplicable()) {
                    final StreamingOutputMarshaller marshaller = new StreamingOutputMarshaller(context,
                            singleEntityResponse(context, entity, uriInfo),
                            headers.getAcceptableMediaTypes(),
                            fieldsFilteringValidator.getFilter());
                    return Response.ok(marshaller).build();
                }
            }

            return Response.ok(new StreamingOutputMarshaller(context, singleEntityResponse(context, entity, uriInfo), headers.getAcceptableMediaTypes())).build();
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    protected Response createInternal(String version, String persistenceUnit, String type, HttpHeaders headers, UriInfo uriInfo, InputStream in) {
        JPARSLogger.entering(CLASS_NAME, "createInternal", new Object[] { "PUT", headers.getMediaType(), version, persistenceUnit, type, uriInfo.getRequestUri().toASCIIString() });
        try {
            final PersistenceContext context = getPersistenceContext(persistenceUnit, type, uriInfo.getBaseUri(), version, null);
            final ClassDescriptor descriptor = context.getDescriptor(type);
            if (descriptor == null) {
                JPARSLogger.error(context.getSessionLog(), "jpars_could_not_find_class_in_persistence_unit", new Object[] { type, persistenceUnit });
                throw JPARSException.classOrClassDescriptorCouldNotBeFoundForEntity(type, persistenceUnit);
            }

            final Object entity = context.unmarshalEntity(type, mediaType(headers.getAcceptableMediaTypes()), in);

            // Check idempotence of the entity
            if (!checkIdempotence(descriptor, entity)) {
                JPARSLogger.error(context.getSessionLog(), "jpars_put_not_idempotent", new Object[]{type, persistenceUnit});
                throw JPARSException.entityIsNotIdempotent(type, persistenceUnit);
            }

            // Check idempotence of the entity's relationships
            if (!checkIdempotenceOnRelationships(descriptor, entity)) {
                JPARSLogger.error(context.getSessionLog(), "jpars_put_not_idempotent", new Object[]{type, persistenceUnit});
                throw JPARSException.entityIsNotIdempotent(type, persistenceUnit);
            }

            // Cascade persist. Sets references to the parent object in collections with objects passed by value.
            if (context.getServiceVersion().compareTo(ServiceVersion.VERSION_2_0) >= 0) {
                processBidirectionalRelationships(context, descriptor, entity);
            }

            // No sequencing in relationships, we can create the object now...
            context.create(getMatrixParameters(uriInfo, persistenceUnit), entity);
            final ResponseBuilder rb = Response.status(Status.CREATED);
            return rb.entity(new StreamingOutputMarshaller(context, singleEntityResponse(context, entity, uriInfo), headers.getAcceptableMediaTypes())).build();
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    /**
     * Finds all bidirectional relationships of the given entity with Cascade=PERSIST and sets reference to the parent
     * object.
     * This method is called on creating new entities in JPARS v2.0 only.
     *
     * @param context       the persistence context.
     * @param descriptor    descriptor of the entity passed in 'entity' parameter.
     * @param entity        entity to process.
     */
    private void processBidirectionalRelationships(PersistenceContext context, ClassDescriptor descriptor, Object entity) {
        final List<DatabaseMapping> mappings = descriptor.getMappings();
        for (DatabaseMapping mapping : mappings) {
            if ((mapping != null) && (mapping instanceof ForeignReferenceMapping)) {
                final ForeignReferenceMapping jpaMapping = (ForeignReferenceMapping) mapping;
                final Object attributeValue = mapping.getAttributeAccessor().getAttributeValueFromObject(entity);
                if (jpaMapping.isCascadePersist()) {
                    if (jpaMapping.getMappedBy() != null) {
                        final ClassDescriptor inverseDescriptor = context.getDescriptor(jpaMapping.getReferenceDescriptor().getAlias());
                        if (inverseDescriptor != null) {
                            final DatabaseMapping inverseMapping = inverseDescriptor.getMappingForAttributeName(jpaMapping.getMappedBy());
                            if (inverseMapping != null) {
                                if (attributeValue != null) {
                                    if (attributeValue instanceof ValueHolder) {
                                        final ValueHolder holder = (ValueHolder) attributeValue;
                                        final Object obj = holder.getValue();
                                        if (obj != null) {
                                            inverseMapping.setAttributeValueInObject(obj, entity);
                                        }
                                    } else if (attributeValue instanceof Collection) {
                                        final Collection<?> collection = (Collection<?>) attributeValue;
                                        if (!collection.isEmpty()) {
                                            for (Object obj : collection) {
                                                inverseMapping.setAttributeValueInObject(obj, entity);
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
    }

    /**
     * This method maintains idempotence on PUT by disallowing sequencing in relationships.
     *
     * @param descriptor    descriptor of the entity passed in 'entity' parameter.
     * @param entity        entity to process.
     * @return true if check is passed (no sequencing)
     */
    private boolean checkIdempotenceOnRelationships(ClassDescriptor descriptor, Object entity) {
        final List<DatabaseMapping> mappings = descriptor.getMappings();
        if ((mappings != null) && (!mappings.isEmpty())) {
            for (DatabaseMapping mapping : mappings) {
                if (mapping instanceof ForeignReferenceMapping) {
                    final ForeignReferenceMapping fkMapping = (ForeignReferenceMapping) mapping;
                    if ((fkMapping.isCascadePersist()) || (fkMapping.isCascadeMerge())) {
                        final ClassDescriptor referenceDescriptor = fkMapping.getReferenceDescriptor();
                        if (referenceDescriptor != null) {
                            if (referenceDescriptor instanceof RelationalDescriptor) {
                                final RelationalDescriptor relDesc = (RelationalDescriptor) referenceDescriptor;
                                final AbstractDirectMapping relSequenceMapping = relDesc.getObjectBuilder().getSequenceMapping();
                                if (relSequenceMapping != null) {
                                    final Object value = mapping.getAttributeAccessor().getAttributeValueFromObject(entity);
                                    if (value != null) {
                                        if (value instanceof ValueHolder) {
                                            final ValueHolder holder = (ValueHolder) value;
                                            if (holder.getValue() != null) {
                                                return false;
                                            }
                                        } else if (value instanceof Collection) {
                                            if (!(((Collection<?>) value).isEmpty())) {
                                                return false;
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
        return true;
    }

    /**
     * This method maintains idempotence on PUT by disallowing sequencing.
     *
     * @param descriptor    descriptor of the entity passed in 'entity' parameter.
     * @param entity        entity to process.
     * @return true if check is passed (no sequencing)
     */
    private boolean checkIdempotence(ClassDescriptor descriptor, Object entity) {
        final AbstractDirectMapping sequenceMapping = descriptor.getObjectBuilder().getSequenceMapping();
        if (sequenceMapping != null) {
            final Object value = sequenceMapping.getAttributeAccessor().getAttributeValueFromObject(entity);
            if (descriptor.getObjectBuilder().isPrimaryKeyComponentInvalid(value, descriptor.getPrimaryKeyFields().indexOf(descriptor.getSequenceNumberField()))
                    || descriptor.getSequence().shouldAlwaysOverrideExistingValue()) {
                return false;
            }
        }
        return true;
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
                JPARSLogger.error(context.getSessionLog(), "jpars_could_not_find_appropriate_mapping_for_update", new Object[] { attribute, type, id, persistenceUnit });
                throw JPARSException.databaseMappingCouldNotBeFoundForEntityAttribute(attribute, type, id, persistenceUnit);
            }
            Object entity = context.unmarshalEntity(mapping.getReferenceDescriptor().getAlias(), mediaType(headers.getAcceptableMediaTypes()), in);
            Object result = context.updateOrAddAttribute(getMatrixParameters(uriInfo, persistenceUnit), type, entityId, getQueryParameters(uriInfo), attribute, entity, partner);
            if (result == null) {
                JPARSLogger.error(context.getSessionLog(), "jpars_could_not_update_attribute", new Object[] { attribute, type, id, persistenceUnit });
                throw JPARSException.attributeCouldNotBeUpdated(attribute, type, id, persistenceUnit);
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
                throw JPARSException.invalidRemoveAttributeRequest(null, type, id, persistenceUnit);
            }

            String partner = getRelationshipPartner(matrixParams, queryParams);
            PersistenceContext context = getPersistenceContext(persistenceUnit, type, uriInfo.getBaseUri(), version, null);
            Object entityId = IdHelper.buildId(context, type, id);
            ClassDescriptor descriptor = context.getDescriptor(type);
            DatabaseMapping mapping = descriptor.getMappingForAttributeName(attribute);
            if (!mapping.isForeignReferenceMapping()) {
                JPARSLogger.error(context.getSessionLog(), "jpars_could_not_find_appropriate_mapping_for_update", new Object[] { attribute, type, id, persistenceUnit });
                throw JPARSException.databaseMappingCouldNotBeFoundForEntityAttribute(attribute, type, id, persistenceUnit);
            }

            Map<String, String> discriminators = getMatrixParameters(uriInfo, persistenceUnit);
            Object entity = context.find(discriminators, type, entityId, getQueryParameters(uriInfo));
            Object result = context.removeAttribute(getMatrixParameters(uriInfo, persistenceUnit), type, entityId, attribute, listItemId, entity, partner);

            if (result == null) {
                JPARSLogger.error(context.getSessionLog(), "jpars_could_not_update_attribute", new Object[] { attribute, type, id, persistenceUnit });
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

    protected Response buildEntityOptionsResponse(String version, String persistenceUnit, String entityName, HttpHeaders httpHeaders, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "buildEntityOptionsResponse", new Object[]{"GET", version, persistenceUnit, entityName, uriInfo.getRequestUri().toASCIIString()});
        final PersistenceContext context = getPersistenceContext(persistenceUnit, null, uriInfo.getBaseUri(), version, null);

        // We need to make sure that entity with given name exists
        final ClassDescriptor descriptor = context.getServerSession().getDescriptorForAlias(entityName);
        if (descriptor == null) {
            JPARSLogger.error(context.getSessionLog(), "jpars_could_not_find_entity_type", new Object[]{entityName, persistenceUnit});
            throw JPARSException.classOrClassDescriptorCouldNotBeFoundForEntity(entityName, persistenceUnit);
        }

        final String linkValue = "<" + HrefHelper.buildEntityMetadataHref(context, entityName) + ">; rel=describedby";
        httpHeaders.getRequestHeaders().putSingle("Link", linkValue);

        return Response.ok()
                .header("Link", linkValue)
                .build();
    }

    private Response findAttributeResponse(PersistenceContext context,
                                           String attribute,
                                           String entityType,
                                           String id,
                                           String persistenceUnit,
                                           Object queryResults,
                                           Map<String, Object> queryParams,
                                           HttpHeaders headers, UriInfo uriInfo,
                                           FeatureResponseBuilder responseBuilder,
                                           FieldsFilter filter) {
        if (queryResults != null) {
            Object results = responseBuilder.buildAttributeResponse(context, queryParams, attribute, queryResults, uriInfo);
            if (results != null) {
                return Response.ok(new StreamingOutputMarshaller(context, results, headers.getAcceptableMediaTypes(), filter)).build();
            } else {
                // something is wrong with the descriptors
                throw JPARSException.responseCouldNotBeBuiltForFindAttributeRequest(attribute, entityType, id, persistenceUnit);
            }
        }
        return Response.ok(new StreamingOutputMarshaller(context, null, headers.getAcceptableMediaTypes(), filter)).build();
    }

    private void checkOrderBy(PersistenceContext context, ReadQuery query) {
        if (query.isReadAllQuery()) {
            ReadAllQuery readAllQuery = (ReadAllQuery) query;
            List<Expression> orderBy = readAllQuery.getOrderByExpressions();
            if ((orderBy == null) || (orderBy.isEmpty())) {
                JPARSLogger.warning(context.getSessionLog(), "no_orderby_clause_for_paging", new Object[] { query.toString() });
            }
        }
    }

    private Object singleEntityResponse(PersistenceContext context, Object entity, UriInfo uriInfo) {
        FeatureSet featureSet = context.getSupportedFeatureSet();
        FeatureResponseBuilder responseBuilder = featureSet.getResponseBuilder(Feature.NO_PAGING);
        return responseBuilder.buildSingleEntityResponse(context, getQueryParameters(uriInfo), entity, uriInfo);
    }
}
