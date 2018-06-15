/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.jpa.rs.resources.common;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.eis.mappings.EISCompositeCollectionMapping;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.MapEntryExpression;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Attribute;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Descriptor;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkTemplate;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.PersistenceUnit;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Query;
import org.eclipse.persistence.internal.queries.MapContainerPolicy;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.StreamingOutputMarshaller;
import org.eclipse.persistence.jpa.rs.util.list.QueryList;
import org.eclipse.persistence.mappings.CollectionMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * @author gonural
 *
 */
public class AbstractPersistenceUnitResource extends AbstractResource {
    private static final String CLASS_NAME = AbstractPersistenceUnitResource.class.getName();

    protected Response getDescriptorMetadataInternal(String version, String persistenceUnit, String descriptorAlias, HttpHeaders headers, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "getDescriptorMetadataInternal", new Object[] { "GET", version, persistenceUnit, descriptorAlias, uriInfo.getRequestUri().toASCIIString() });

        String result = null;
        try {
            URI baseURI = uriInfo.getBaseUri();
            PersistenceContext context = getPersistenceContext(persistenceUnit, null, baseURI, version, null);
            ClassDescriptor descriptor = context.getServerSession().getDescriptorForAlias(descriptorAlias);
            if (descriptor == null) {
                JPARSLogger.error(context.getSessionLog(), "jpars_could_not_find_entity_type", new Object[] { descriptorAlias, persistenceUnit });
                throw JPARSException.classOrClassDescriptorCouldNotBeFoundForEntity(descriptorAlias, persistenceUnit);
            } else {
                String mediaType = StreamingOutputMarshaller.mediaType(headers.getAcceptableMediaTypes()).toString();
                Descriptor returnDescriptor = buildDescriptor(context, persistenceUnit, descriptor, baseURI.toString());
                result = marshallMetadata(returnDescriptor, mediaType);
            }
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
        return Response.ok(new StreamingOutputMarshaller(null, result, headers.getAcceptableMediaTypes())).build();
    }

    protected Response getQueriesMetadataInternal(String version, String persistenceUnit, HttpHeaders headers, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "getQueriesMetadataInternal", new Object[] { "GET", version, persistenceUnit, uriInfo.getRequestUri().toASCIIString() });

        try {
            PersistenceContext context = getPersistenceContext(persistenceUnit, null, uriInfo.getBaseUri(), version, null);
            List<Query> queries = new ArrayList<Query>();
            addQueries(queries, context, null);
            String mediaType = StreamingOutputMarshaller.mediaType(headers.getAcceptableMediaTypes()).toString();
            QueryList queryList = new QueryList();
            queryList.setList(queries);
            String result = null;
            if (mediaType.equals(MediaType.APPLICATION_JSON)) {
                result = marshallMetadata(queryList.getList(), mediaType);
            } else {
                result = marshallMetadata(queryList, mediaType);
            }
            return Response.ok(new StreamingOutputMarshaller(null, result, headers.getAcceptableMediaTypes())).build();
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    protected Response getQueryMetadataInternal(String version, String persistenceUnit, String queryName, HttpHeaders headers, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "getQueryMetadataInternal", new Object[] { "GET", version, persistenceUnit, queryName, uriInfo.getRequestUri().toASCIIString() });
        try {
            PersistenceContext context = getPersistenceContext(persistenceUnit, null, uriInfo.getBaseUri(), version, null);
            List<Query> returnQueries = new ArrayList<Query>();
            Map<String, List<DatabaseQuery>> queries = context.getServerSession().getQueries();
            if (queries.get(queryName) != null) {
                for (DatabaseQuery query : queries.get(queryName)) {
                    returnQueries.add(getQuery(query, context));
                }
            }
            String mediaType = StreamingOutputMarshaller.mediaType(headers.getAcceptableMediaTypes()).toString();
            QueryList queryList = new QueryList();
            queryList.setList(returnQueries);
            String result = null;
            if (mediaType.equals(MediaType.APPLICATION_JSON)) {
                result = marshallMetadata(queryList.getList(), mediaType);
            } else {
                result = marshallMetadata(queryList, mediaType);
            }
            return Response.ok(new StreamingOutputMarshaller(null, result, headers.getAcceptableMediaTypes())).build();

        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    @SuppressWarnings("rawtypes")
    protected Response getTypesInternal(String version, String persistenceUnit, HttpHeaders headers, UriInfo uriInfo) {
        JPARSLogger.entering(CLASS_NAME, "getTypesInternal", new Object[] { "GET", version, persistenceUnit, uriInfo.getRequestUri().toASCIIString() });
        try {
            URI baseURI = uriInfo.getBaseUri();
            PersistenceContext context = getPersistenceContext(persistenceUnit, null, baseURI, version, null);
            PersistenceUnit pu = new PersistenceUnit();
            pu.setPersistenceUnitName(persistenceUnit);
            Map<Class, ClassDescriptor> descriptors = context.getServerSession().getDescriptors();
            String mediaType = StreamingOutputMarshaller.mediaType(headers.getAcceptableMediaTypes()).toString();
            Iterator<Class> contextIterator = descriptors.keySet().iterator();
            while (contextIterator.hasNext()) {
                ClassDescriptor descriptor = descriptors.get(contextIterator.next());
                String alias = descriptor.getAlias();
                if (descriptor.isAggregateDescriptor()) {
                    // skip embeddables
                    continue;
                }
                if (version != null) {
                    pu.getTypes().add(new Link(alias, mediaType, baseURI + version + "/" + persistenceUnit + "/metadata/entity/" + alias));
                } else {
                    pu.getTypes().add(new Link(alias, mediaType, baseURI + persistenceUnit + "/metadata/entity/" + alias));
                }
            }
            String result = marshallMetadata(pu, mediaType);
            return Response.ok(new StreamingOutputMarshaller(null, result, headers.getAcceptableMediaTypes())).build();
        } catch (Exception ex) {
            throw JPARSException.exceptionOccurred(ex);
        }
    }

    @SuppressWarnings("rawtypes")
    private void addMapping(Descriptor descriptor, DatabaseMapping mapping) {
        String target = null;
        String collectionName = null;
        if (mapping.isCollectionMapping()) {
            if (mapping.isEISMapping()) {
                EISCompositeCollectionMapping collectionMapping = (EISCompositeCollectionMapping) mapping;
                Class collectionClass = collectionMapping.getContainerPolicy().getContainerClass();
                String collectionType = getSimplePublicCollectionTypeName(collectionClass);
                if (collectionType == null) {
                    collectionType = collectionClass.getSimpleName();
                }
                if (collectionMapping.getReferenceClass() != null) {
                    collectionName = collectionMapping.getReferenceClass().getSimpleName();
                }
                if ((collectionName == null) && (collectionMapping.getAttributeClassification() != null)) {
                    collectionName = collectionMapping.getAttributeClassification().getSimpleName();
                }
                if (collectionMapping.getContainerPolicy().isMapPolicy()) {
                    String mapKeyType = ((MapContainerPolicy) collectionMapping.getContainerPolicy()).getKeyType().getClass().getSimpleName();
                    target = collectionType + "<" + mapKeyType + ", " + collectionName + ">";
                } else {
                    target = collectionType + "<" + collectionName + ">";
                }
            } else {
                CollectionMapping collectionMapping = (CollectionMapping) mapping;
                Class collectionClass = collectionMapping.getContainerPolicy().getContainerClass();

                String collectionType = getSimplePublicCollectionTypeName(collectionClass);
                if (collectionType == null) {
                    collectionType = collectionClass.getSimpleName();
                }

                if (collectionMapping.getReferenceClass() != null) {
                    collectionName = collectionMapping.getReferenceClass().getSimpleName();
                }
                if ((collectionName == null) && (collectionMapping.getAttributeClassification() != null)) {
                    collectionName = collectionMapping.getAttributeClassification().getSimpleName();
                }
                if (collectionMapping.getContainerPolicy().isMapPolicy()) {
                    String mapKeyType = ((MapContainerPolicy) collectionMapping.getContainerPolicy()).getKeyType().getClass().getSimpleName();
                    target = collectionType + "<" + mapKeyType + ", " + collectionName + ">";
                } else {
                    target = collectionType + "<" + collectionName + ">";
                }
            }
        } else if (mapping.isForeignReferenceMapping()) {
            target = ((ForeignReferenceMapping) mapping).getReferenceClass().getSimpleName();
        } else {
            target = mapping.getAttributeClassification().getSimpleName();
        }

        descriptor.getAttributes().add(new Attribute(mapping.getAttributeName(), target));
    }

    private void addQueries(List<Query> queryList, PersistenceContext context, String javaClassName) {
        Map<String, List<DatabaseQuery>> queries = context.getServerSession().getQueries();
        List<DatabaseQuery> returnQueries = new ArrayList<DatabaseQuery>();
        for (List<DatabaseQuery> keyQueries : queries.values()) {
            Iterator<DatabaseQuery> queryIterator = keyQueries.iterator();
            while (queryIterator.hasNext()) {
                DatabaseQuery query = queryIterator.next();
                if (javaClassName == null || (query.getReferenceClassName() != null && query.getReferenceClassName().equals(javaClassName))) {
                    returnQueries.add(query);
                }
            }
        }
        Iterator<DatabaseQuery> queryIterator = returnQueries.iterator();
        while (queryIterator.hasNext()) {
            queryList.add(getQuery(queryIterator.next(), context));
        }
    }

    private Descriptor buildDescriptor(PersistenceContext context, String persistenceUnit, ClassDescriptor descriptor, String baseUri) {
        Descriptor returnDescriptor = new Descriptor();
        String name = descriptor.getAlias();
        returnDescriptor.setName(name);

        String version = context.getVersion();
        if (version != null) {
            version = version + "/";
            returnDescriptor.getLinkTemplates().add(new LinkTemplate("find", "get", baseUri + version + persistenceUnit + "/entity/" + descriptor.getAlias() + "/{primaryKey}"));
            returnDescriptor.getLinkTemplates().add(new LinkTemplate("persist", "put", baseUri + version + persistenceUnit + "/entity/" + descriptor.getAlias()));
            returnDescriptor.getLinkTemplates().add(new LinkTemplate("update", "post", baseUri + version + persistenceUnit + "/entity/" + descriptor.getAlias()));
            returnDescriptor.getLinkTemplates().add(new LinkTemplate("delete", "delete", baseUri + version + persistenceUnit + "/entity/" + descriptor.getAlias() + "/{primaryKey}"));
        } else {
            returnDescriptor.getLinkTemplates().add(new LinkTemplate("find", "get", baseUri + persistenceUnit + "/entity/" + descriptor.getAlias() + "/{primaryKey}"));
            returnDescriptor.getLinkTemplates().add(new LinkTemplate("persist", "put", baseUri + persistenceUnit + "/entity/" + descriptor.getAlias()));
            returnDescriptor.getLinkTemplates().add(new LinkTemplate("update", "post", baseUri + persistenceUnit + "/entity/" + descriptor.getAlias()));
            returnDescriptor.getLinkTemplates().add(new LinkTemplate("delete", "delete", baseUri + persistenceUnit + "/entity/" + descriptor.getAlias() + "/{primaryKey}"));
        }
        if (!descriptor.getMappings().isEmpty()) {
            Iterator<DatabaseMapping> mappingIterator = descriptor.getMappings().iterator();
            while (mappingIterator.hasNext()) {
                DatabaseMapping mapping = mappingIterator.next();
                addMapping(returnDescriptor, mapping);
            }
        }
        addQueries(returnDescriptor.getQueries(), context, descriptor.getJavaClassName());
        return returnDescriptor;
    }

    private Query getQuery(DatabaseQuery query, PersistenceContext context) {
        String method = query.isReadQuery() ? "get" : "post";
        String jpql = query.getJPQLString() == null ? "" : query.getJPQLString();
        StringBuilder parameterString = new StringBuilder();
        Iterator<String> argumentsIterator = query.getArguments().iterator();
        while (argumentsIterator.hasNext()) {
            String argument = argumentsIterator.next();
            parameterString.append(";");
            parameterString.append(argument).append("={").append(argument).append("}");
        }

        String version = context.getVersion();
        Query returnQuery = null;
        if (version != null) {
            returnQuery = new Query(query.getName(), jpql, new LinkTemplate("execute", method, context.getBaseURI() + version + "/" + context.getName() + "/query/" + query.getName() + parameterString));
        } else {
            returnQuery = new Query(query.getName(), jpql, new LinkTemplate("execute", method, context.getBaseURI() + context.getName() + "/query/" + query.getName() + parameterString));
        }
        if (query.isReportQuery()) {
            query.checkPrepare((AbstractSession) context.getServerSession(), new DatabaseRecord());
            for (ReportItem item : ((ReportQuery) query).getItems()) {
                if (item.getMapping() != null) {
                    if (item.getAttributeExpression() != null && item.getAttributeExpression().isMapEntryExpression()) {
                        if (((MapEntryExpression) item.getAttributeExpression()).shouldReturnMapEntry()) {
                            returnQuery.getReturnTypes().add(Map.Entry.class.getSimpleName());
                        } else {
                            returnQuery.getReturnTypes().add(((Class<?>) ((CollectionMapping) item.getMapping()).getContainerPolicy().getKeyType()).getSimpleName());
                        }
                    } else {
                        returnQuery.getReturnTypes().add(item.getMapping().getAttributeClassification().getSimpleName());
                    }
                } else if (item.getResultType() != null) {
                    returnQuery.getReturnTypes().add(item.getResultType().getSimpleName());
                } else if (item.getDescriptor() != null) {
                    returnQuery.getReturnTypes().add(item.getDescriptor().getJavaClass().getSimpleName());
                } else if (item.getAttributeExpression() != null && item.getAttributeExpression().isConstantExpression()) {
                    returnQuery.getReturnTypes().add(((ConstantExpression) item.getAttributeExpression()).getValue().getClass().getSimpleName());
                } else {
                    // Use Object.class by default.
                    returnQuery.getReturnTypes().add(ClassConstants.OBJECT.getSimpleName());
                }
            }
        } else {
            returnQuery.getReturnTypes().add(query.getReferenceClassName() == null ? "" : query.getReferenceClass().getSimpleName());
        }
        return returnQuery;
    }

    private String getSimplePublicCollectionTypeName(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        LinkedHashSet<Class<?>> all = new LinkedHashSet<Class<?>>();
        getInterfaces(clazz, all);
        ArrayList<Class<?>> list = new ArrayList<Class<?>>(all);
        for (int i = 0; i < all.size(); i++) {
            Class<?> clas = list.get(i);
            if (clas.getName().equals(List.class.getName())) {
                return List.class.getSimpleName();
            }
            if (clas.getName().equals(Map.class.getName())) {
                return Map.class.getSimpleName();
            }
            if (clas.getName().equals(Set.class.getName())) {
                return Set.class.getSimpleName();
            }
        }
        return null;
    }

    private void getInterfaces(Class<?> clazz, HashSet<Class<?>> interfaceList) {
        // java.lang.Class.getInterfaces returns all directly implemented interfaces
        // and it doesn't walk the hierarchy to get all interfaces of all parent types.
        // we walk through the hierarchy here
        while (clazz != null) {
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> i : interfaces) {
                if (interfaceList.add(i)) {
                    getInterfaces(i, interfaceList);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
