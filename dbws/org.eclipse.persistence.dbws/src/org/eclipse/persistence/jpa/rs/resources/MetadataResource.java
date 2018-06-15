/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Dmitry Kornilov - Initial implementation
package org.eclipse.persistence.jpa.rs.resources;

import static org.eclipse.persistence.jpa.rs.resources.common.AbstractResource.SERVICE_VERSION_FORMAT;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.mappings.EISCompositeCollectionMapping;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.MapEntryExpression;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.v2.MetadataCatalog;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.v2.Property;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.v2.Reference;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.v2.Resource;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.v2.ResourceSchema;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.features.ItemLinksBuilder;
import org.eclipse.persistence.jpa.rs.resources.common.AbstractResource;
import org.eclipse.persistence.jpa.rs.util.HrefHelper;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * JPARS 2.0 metadata catalog. Resource metadata and schemas.
 *
 * @author Dmitry Kornilov
 * @since EclipseLink 2.6.0.
 */
@Produces({ MediaType.APPLICATION_JSON })
@Consumes({ MediaType.APPLICATION_JSON })
@Path("/{version : " + SERVICE_VERSION_FORMAT + "}/{context}/metadata-catalog/")
public class MetadataResource extends AbstractResource {
    private static final String CLASS_NAME = MetadataResource.class.getName();
    private static final Map<Class<?>, String> PRIMITIVE_TO_JSON = new HashMap<Class<?>, String>();

    static {
        PRIMITIVE_TO_JSON.put(boolean.class, "boolean");
        PRIMITIVE_TO_JSON.put(byte.class, "number");
        PRIMITIVE_TO_JSON.put(char.class, "string");
        PRIMITIVE_TO_JSON.put(double.class, "number");
        PRIMITIVE_TO_JSON.put(float.class, "number");
        PRIMITIVE_TO_JSON.put(int.class, "integer");
        PRIMITIVE_TO_JSON.put(long.class, "integer");
        PRIMITIVE_TO_JSON.put(short.class, "number");
        PRIMITIVE_TO_JSON.put(short.class, "number");
    }

    /**
     * Returns metadata catalog.
     */
    @GET
    public Response getMetadataCatalog(@PathParam("version") String version,
                                       @PathParam("context") String persistenceUnit,
                                       @Context HttpHeaders httpHeaders,
                                       @Context UriInfo uriInfo) {
        setRequestUniqueId();
        return buildMetadataCatalogResponse(version, persistenceUnit, httpHeaders, uriInfo);
    }

    /**
     * Returns entity metadata if accepted media type is 'application/json' or entity schema if
     * accepted media type is 'application/schema+json'.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, AbstractResource.APPLICATION_SCHEMA_JSON })
    @Consumes({ MediaType.APPLICATION_JSON, AbstractResource.APPLICATION_SCHEMA_JSON })
    @Path("entity/{entityName}")
    public Response getEntityResource(@PathParam("version") String version,
                                      @PathParam("context") String persistenceUnit,
                                      @PathParam("entityName") String entityName,
                                      @Context HttpHeaders httpHeaders,
                                      @Context UriInfo uriInfo) {
        setRequestUniqueId();
        final MediaType mediaType = StreamingOutputMarshaller.mediaType(httpHeaders.getAcceptableMediaTypes());

        // Return schema if application.schema+json media type is requested otherwise return entity metadata
        if (mediaType.equals(AbstractResource.APPLICATION_SCHEMA_JSON_TYPE)) {
            return buildEntitySchemaResponse(version, persistenceUnit, entityName, uriInfo);
        } else {
            return buildEntityMetadataResponse(version, persistenceUnit, entityName, httpHeaders, uriInfo);
        }
    }

    /**
     * Returns query metadata if accepted media type is 'application/json' or entity schema if
     * accepted media type is 'application/schema+json'.
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, AbstractResource.APPLICATION_SCHEMA_JSON })
    @Consumes({ MediaType.APPLICATION_JSON, AbstractResource.APPLICATION_SCHEMA_JSON })
    @Path("query/{queryName}")
    public Response getQueryResource(@PathParam("version") String version,
                                     @PathParam("context") String persistenceUnit,
                                     @PathParam("queryName") String queryName,
                                     @Context HttpHeaders httpHeaders,
                                     @Context UriInfo uriInfo) {
        setRequestUniqueId();
        final MediaType mediaType = StreamingOutputMarshaller.mediaType(httpHeaders.getAcceptableMediaTypes());

        // Return schema if application.schema+json media type is requested otherwise return entity metadata
        if (mediaType.equals(AbstractResource.APPLICATION_SCHEMA_JSON_TYPE)) {
            return buildQuerySchemaResponse(version, persistenceUnit, queryName, uriInfo);
        } else {
            return buildQueryMetadataResponse(version, persistenceUnit, queryName, httpHeaders, uriInfo);
        }
    }

    private Response buildMetadataCatalogResponse(String version, String persistenceUnit, HttpHeaders httpHeaders, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "buildMetadataCatalogResponse", new Object[]{"GET", version, persistenceUnit, uriInfo.getRequestUri().toASCIIString()});

        final String result;
        try {
            final PersistenceContext context = getPersistenceContext(persistenceUnit, null, uriInfo.getBaseUri(), version, null);
            final MetadataCatalog catalog = buildMetadataCatalog(context);
            final String mediaType = StreamingOutputMarshaller.mediaType(httpHeaders.getAcceptableMediaTypes()).toString();
            result = marshallMetadata(catalog, mediaType);
        } catch (JAXBException e) {
            throw JPARSException.exceptionOccurred(e);
        }
        return Response.ok(new StreamingOutputMarshaller(null, result, httpHeaders.getAcceptableMediaTypes())).build();
    }

    private Response buildEntityMetadataResponse(String version, String persistenceUnit, String entityName, HttpHeaders httpHeaders, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "buildEntityMetadataResponse", new Object[]{"GET", version, persistenceUnit, entityName, uriInfo.getRequestUri().toASCIIString()});

        final String result;
        try {
            final PersistenceContext context = getPersistenceContext(persistenceUnit, null, uriInfo.getBaseUri(), version, null);
            final ClassDescriptor descriptor = context.getServerSession().getDescriptorForAlias(entityName);
            if (descriptor == null) {
                JPARSLogger.error(context.getSessionLog(), "jpars_could_not_find_entity_type", new Object[] { entityName, persistenceUnit });
                throw JPARSException.classOrClassDescriptorCouldNotBeFoundForEntity(entityName, persistenceUnit);
            } else {
                final String mediaType = StreamingOutputMarshaller.mediaType(httpHeaders.getAcceptableMediaTypes()).toString();
                final Resource resource = buildEntityMetadata(context, descriptor);
                result = marshallMetadata(resource, mediaType);
            }
        } catch (JAXBException e) {
            throw JPARSException.exceptionOccurred(e);
        }
        return Response.ok(new StreamingOutputMarshaller(null, result, httpHeaders.getAcceptableMediaTypes())).build();
    }

    private Response buildQueryMetadataResponse(String version, String persistenceUnit, String queryName, HttpHeaders httpHeaders, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "buildQueryMetadataResponse", new Object[]{"GET", version, persistenceUnit, queryName, uriInfo.getRequestUri().toASCIIString()});

        final String result;
        try {
            final PersistenceContext context = getPersistenceContext(persistenceUnit, null, uriInfo.getBaseUri(), version, null);

            // We need to make sure that query with given name exists
            final DatabaseQuery query = context.getServerSession().getQuery(queryName);
            if (query == null) {
                JPARSLogger.error(context.getSessionLog(), "jpars_could_not_find_query", new Object[] {queryName, persistenceUnit});
                throw JPARSException.responseCouldNotBeBuiltForNamedQueryRequest(queryName, context.getName());
            }

            final String mediaType = StreamingOutputMarshaller.mediaType(httpHeaders.getAcceptableMediaTypes()).toString();
            final Resource resource = buildQueryMetadata(context, query);
            result = marshallMetadata(resource, mediaType);
        } catch (JAXBException e) {
            throw JPARSException.exceptionOccurred(e);
        }
        return Response.ok(new StreamingOutputMarshaller(null, result, httpHeaders.getAcceptableMediaTypes())).build();
    }

    private Response buildEntitySchemaResponse(String version, String persistenceUnit, String entityName, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "buildEntitySchemaResponse", new Object[]{"GET", version, persistenceUnit, uriInfo.getRequestUri().toASCIIString()});

        final String result;
        try {
            final PersistenceContext context = getPersistenceContext(persistenceUnit, null, uriInfo.getBaseUri(), version, null);
            final ClassDescriptor descriptor = context.getServerSession().getDescriptorForAlias(entityName);
            if (descriptor == null) {
                JPARSLogger.error(context.getSessionLog(), "jpars_could_not_find_entity_type", new Object[] { entityName, persistenceUnit });
                throw JPARSException.classOrClassDescriptorCouldNotBeFoundForEntity(entityName, persistenceUnit);
            } else {
                final ResourceSchema schema = new ResourceSchema();
                schema.setTitle(descriptor.getAlias());
                schema.setSchema(HrefHelper.buildEntityMetadataHref(context, descriptor.getAlias()) + "#");
                schema.addAllOf(new Reference(HrefHelper.buildBaseRestSchemaRef("#/singularResource")));

                // Properties
                for (DatabaseMapping databaseMapping : descriptor.getMappings()) {
                    schema.addProperty(databaseMapping.getAttributeName(), buildProperty(context, databaseMapping));
                }

                // Links
                final String instancesHref = HrefHelper.buildEntityDescribesHref(context, descriptor.getAlias());
                schema.setLinks((new ItemLinksBuilder())
                        .addDescribedBy(HrefHelper.buildEntityMetadataHref(context, descriptor.getAlias()))
                        .addFind(instancesHref + "/{primaryKey}")
                        .addCreate(instancesHref)
                        .addUpdate(instancesHref)
                        .addDelete(instancesHref + "/{primaryKey}")
                        .getList());

                result = marshallMetadata(schema, MediaType.APPLICATION_JSON);
            }
        } catch (JAXBException e) {
            throw JPARSException.exceptionOccurred(e);
        }
        return Response.ok(new StreamingOutputMarshaller(null, result, AbstractResource.APPLICATION_SCHEMA_JSON_TYPE)).build();
    }

    private Response buildQuerySchemaResponse(String version, String persistenceUnit, String queryName, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "buildQuerySchemaResponse", new Object[]{"GET", version, persistenceUnit, uriInfo.getRequestUri().toASCIIString()});

        final String result;
        try {
            final PersistenceContext context = getPersistenceContext(persistenceUnit, null, uriInfo.getBaseUri(), version, null);

            // We need to make sure that query with given name exists
            final DatabaseQuery query = context.getServerSession().getQuery(queryName);
            if (query == null) {
                JPARSLogger.error(context.getSessionLog(), "jpars_could_not_find_query", new Object[] {queryName, persistenceUnit});
                throw JPARSException.responseCouldNotBeBuiltForNamedQueryRequest(queryName, context.getName());
            }

            final ResourceSchema querySchema = buildQuerySchema(context, query);
            result = marshallMetadata(querySchema, MediaType.APPLICATION_JSON);
        } catch (JAXBException e) {
            throw JPARSException.exceptionOccurred(e);
        }
        return Response.ok(new StreamingOutputMarshaller(null, result, AbstractResource.APPLICATION_SCHEMA_JSON_TYPE)).build();
    }

    private MetadataCatalog buildMetadataCatalog(PersistenceContext context) {
        final MetadataCatalog result = new MetadataCatalog();

        // Entities
        final Map<Class, ClassDescriptor> descriptors = context.getServerSession().getDescriptors();
        for (ClassDescriptor descriptor : descriptors.values()) {

            // Skip embeddables
            if (!descriptor.isAggregateDescriptor()) {
                result.addResource(buildEntityMetadata(context, descriptor));
            }
        }

        // Queries
        final Map<String, List<DatabaseQuery>> allQueries = context.getServerSession().getQueries();
        for (List<DatabaseQuery> databaseQueries : allQueries.values()) {
            if (databaseQueries != null) {
                for (DatabaseQuery query : databaseQueries) {
                    if (query.getReferenceClassName() != null) {
                        result.addResource(buildQueryMetadata(context, query));
                    }
                }
            }
        }

        final String href = HrefHelper.buildMetadataCatalogHref(context);
        final List<LinkV2> links = (new ItemLinksBuilder())
                .addCanonical(href)
                .getList();

        result.setLinks(links);
        return result;
    }

    private Resource buildEntityMetadata(PersistenceContext context, ClassDescriptor descriptor) {
        final Resource resource = new Resource();
        resource.setName(descriptor.getAlias());

        final String metadataHref = HrefHelper.buildEntityMetadataHref(context, descriptor.getAlias());
        final List<LinkV2> links = (new ItemLinksBuilder())
                .addAlternate(metadataHref)
                .addCanonical(metadataHref, MediaType.APPLICATION_JSON)
                .addDescribes(HrefHelper.buildEntityDescribesHref(context, descriptor.getAlias()))
                .getList();

        resource.setLinks(links);
        return resource;
    }

    private Resource buildQueryMetadata(PersistenceContext context, DatabaseQuery query) {
        final Resource resource = new Resource();
        resource.setName(query.getName());

        final String metadataHref = HrefHelper.buildQueryMetadataHref(context, query.getName());
        final List<LinkV2> links = (new ItemLinksBuilder())
                .addAlternate(metadataHref)
                .addCanonical(metadataHref, MediaType.APPLICATION_JSON)
                .addDescribes(HrefHelper.buildQueryDescribesHref(context, query.getName()))
                .getList();

        resource.setLinks(links);
        return resource;
    }

    private ResourceSchema buildQuerySchema(PersistenceContext context, DatabaseQuery query) {
        final ResourceSchema schema = new ResourceSchema();
        schema.setTitle(query.getName());
        schema.setSchema(HrefHelper.buildQueryMetadataHref(context, query.getName()) + "#");
        schema.addAllOf(new Reference(HrefHelper.buildBaseRestSchemaRef("#/collectionBaseResource")));

        // Link
        final String method = query.isReadQuery() ? "GET" : "POST";
        schema.setLinks((new ItemLinksBuilder())
                .addExecute(HrefHelper.buildQueryHref(context, query.getName(), getQueryParamString(query)), method)
                .getList());

        // Definitions
        if (query.isReportQuery()) {
            // In case of report query we need to define a returned type
            final ResourceSchema returnType = new ResourceSchema();

            query.checkPrepare((AbstractSession) context.getServerSession(), new DatabaseRecord());
            for (ReportItem item : ((ReportQuery) query).getItems()) {
                final Property property;
                if (item.getMapping() != null) {
                    if (item.getAttributeExpression() != null && item.getAttributeExpression().isMapEntryExpression()) {
                        if (((MapEntryExpression)item.getAttributeExpression()).shouldReturnMapEntry()) {
                            property = buildProperty(context, Map.Entry.class);
                        } else {
                            property = buildProperty(context, ((Class<?>) item.getMapping().getContainerPolicy().getKeyType()));
                        }
                    } else {
                        property = buildProperty(context, item.getMapping().getAttributeClassification());
                    }
                } else if (item.getResultType() != null) {
                    property = buildProperty(context, item.getResultType());
                } else if (item.getDescriptor() != null) {
                    property = buildProperty(context, item.getDescriptor().getJavaClass());
                } else if (item.getAttributeExpression() != null && item.getAttributeExpression().isConstantExpression()) {
                    property = buildProperty(context, ((ConstantExpression) item.getAttributeExpression()).getValue().getClass());
                } else {
                    // Use Object.class by default.
                    property = buildProperty(context, Object.class);
                }
                returnType.addProperty(item.getName(), property);
            }
            schema.addDefinition("result", returnType);

            final Property items = new Property();
            items.setType("array");
            items.setItems(new Property("#/definitions/result"));
            schema.addProperty("items", items);
        } else {
            // Read all query. Each item is an entity. Make a JSON pointer.
            if (query.getReferenceClassName() != null) {
                final Property items = new Property();
                items.setType("array");
                items.setItems(new Property(HrefHelper.buildEntityMetadataHref(context, query.getReferenceClass().getSimpleName()) + "#"));
                schema.addProperty("items", items);
            }
        }

        return schema;
    }

    private String getQueryParamString(DatabaseQuery query) {
        final StringBuilder queryParams = new StringBuilder();
        for (String arg : query.getArguments()) {
            queryParams.append(";");
            queryParams.append(arg).append("={").append(arg).append("}");
        }
        return queryParams.toString();
    }

    private Property buildProperty(PersistenceContext context, DatabaseMapping mapping) {
        if (mapping.isCollectionMapping()) {
            final Property property = new Property();
            property.setType("array");
            property.setItems(buildProperty(context, getCollectionGenericClass(mapping)));
            return property;
        } else if (mapping.isForeignReferenceMapping()) {
            final ForeignReferenceMapping foreignReferenceMapping = (ForeignReferenceMapping)mapping;
            final String href = HrefHelper.buildEntityMetadataHref(context, foreignReferenceMapping.getReferenceClass().getSimpleName() + "#");
            return new Property(href);
        } else {
            return buildProperty(context, mapping.getAttributeClassification());
        }
    }

    private Property buildProperty(PersistenceContext context, Class<?> clazz) {
        final Property property = new Property();
        if (context.getServerSession().getDescriptorForAlias(clazz.getSimpleName()) != null) {
            property.setRef(HrefHelper.buildEntityMetadataHref(context, clazz.getSimpleName()) + "#");
        } else if (Number.class.isAssignableFrom(clazz)) {
            property.setType("number");
        } else if (Boolean.class.equals(clazz)) {
            property.setType("boolean");
        } else if (String.class.equals(clazz)) {
            property.setType("string");
        } else if (Collection.class.isAssignableFrom(clazz)) {
            property.setType("array");
        } else if (clazz.isPrimitive()) {
            property.setType(PRIMITIVE_TO_JSON.get(clazz));
        } else {
            property.setType("object");
        }

        return property;
    }

    private Class<?> getCollectionGenericClass(DatabaseMapping mapping) {
        Class<?> collectionName = null;
        if (mapping.isEISMapping()) {
            final EISCompositeCollectionMapping collectionMapping = (EISCompositeCollectionMapping) mapping;
            if (collectionMapping.getReferenceClass() != null) {
                collectionName = collectionMapping.getReferenceClass();
            }
            if ((collectionName == null) && (collectionMapping.getAttributeClassification() != null)) {
                collectionName = collectionMapping.getAttributeClassification();
            }
        } else {
            final CollectionMapping collectionMapping = (CollectionMapping) mapping;
            if (collectionMapping.getReferenceClass() != null) {
                collectionName = collectionMapping.getReferenceClass();
            }
            if ((collectionName == null) && (collectionMapping.getAttributeClassification() != null)) {
                collectionName = collectionMapping.getAttributeClassification();
            }
        }

        return collectionName;
    }
}
