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
//     gonural - Initial implementation
package org.eclipse.persistence.jpa.rs.resources.common;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.UUID;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.internal.jpa.rs.metadata.model.Attribute;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Descriptor;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.ItemLinks;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkTemplate;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkV2;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.PersistenceUnit;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Query;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.v2.ContextsCatalog;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.v2.MetadataCatalog;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.v2.Property;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.v2.Reference;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.v2.Resource;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.v2.ResourceSchema;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jpa.rs.DataStorage;
import org.eclipse.persistence.jpa.rs.MatrixParameters;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceContextFactory;
import org.eclipse.persistence.jpa.rs.PersistenceContextFactoryProvider;
import org.eclipse.persistence.jpa.rs.QueryParameters;
import org.eclipse.persistence.jpa.rs.exceptions.JPARSException;
import org.eclipse.persistence.jpa.rs.features.ServiceVersion;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.list.LinkList;
import org.eclipse.persistence.jpa.rs.util.list.QueryList;

/**
 * Base class for all resources.
 *
 * @author gonural
 */
public abstract class AbstractResource {
    public static final String SERVICE_VERSION_FORMAT = "v\\d\\.\\d|latest";
    public static final String APPLICATION_SCHEMA_JSON ="application/schema+json";
    public static final MediaType APPLICATION_SCHEMA_JSON_TYPE = new MediaType("application","schema+json");
    protected PersistenceContextFactory factory;

    /**
     * Sets the persistence factory.
     *
     * @param factory the new persistence factory
     */
    public void setPersistenceFactory(PersistenceContextFactory factory) {
        this.factory = factory;
    }

    /**
     * Gets the persistence factory.
     *
     * @return the persistence factory
     */
    public PersistenceContextFactory getPersistenceFactory() {
        return getPersistenceFactory(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Gets the persistence factory.
     *
     * @return the persistence factory
     */
    public PersistenceContextFactory getPersistenceFactory(ClassLoader loader) {
        if (factory == null) {
            factory = buildPersistenceContextFactory(loader);
        }
        return factory;
    }

    /**
     * Builds the persistence context factory.
     *
     * @param loader the loader
     * @return the persistence context factory
     */
    protected PersistenceContextFactory buildPersistenceContextFactory(ClassLoader loader) {
        ServiceLoader<PersistenceContextFactoryProvider> contextFactoryLoader = ServiceLoader.load(PersistenceContextFactoryProvider.class, loader);

        for (PersistenceContextFactoryProvider provider : contextFactoryLoader) {
            PersistenceContextFactory factory = provider.getPersistenceContextFactory(null);
            if (factory != null) {
                return factory;
            }
        }
        return null;
    }

    /**
     *  Get a map of the matrix parameters associated with the URI path segment of the current request
     *
     *  In JPA-RS, things that user sets (such as parameters of named queries, etc.) are treated as matrix parameters
     *  List of valid matrix parameters for JPA-RS is defined in MatrixParameters
     *  @see         MatrixParameters
     *
     * @param info the info
     * @param segment the segment
     * @return the matrix parameters
     */
    protected static Map<String, String> getMatrixParameters(UriInfo info, String segment) {
        Map<String, String> matrixParameters = new HashMap<String, String>();
        for (PathSegment pathSegment : info.getPathSegments()) {
            if (pathSegment.getPath() != null && pathSegment.getPath().equals(segment)) {
                for (Entry<String, List<String>> entry : pathSegment.getMatrixParameters().entrySet()) {
                    matrixParameters.put(entry.getKey(), entry.getValue().get(0));
                }
                return matrixParameters;
            }
        }
        return matrixParameters;
    }

    /**
     * Get the URI query parameters of the current request
     *
     *  In JPA-RS, predefined attributes (such as eclipselink query hints) are treated as query parameters
     *  List of valid query parameters for JPA-RS is defined in QueryParameters
     *  @see         QueryParameters
     *
     * @param info the info
     * @return the query parameters
     */
    public static Map<String, Object> getQueryParameters(UriInfo info) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        for (String key : info.getQueryParameters().keySet()) {
            queryParameters.put(key, info.getQueryParameters().getFirst(key));
        }
        return queryParameters;
    }

    /**
     * Checks if is valid version.
     *
     * @param version the version
     * @return true, if is valid version
     */
    protected static boolean isValidVersion(String version) {
        return ServiceVersion.hasCode(version);
    }

    /**
     * Gets the persistence context.
     *
     * @param persistenceUnit the persistence unit
     * @param baseURI the base uri
     * @param version the version
     * @param initializationProperties the initialization properties
     * @return the persistence context
     */
    protected PersistenceContext getPersistenceContext(String persistenceUnit, String entityType, URI baseURI, String version, Map<String, Object> initializationProperties) {
        if (!isValidVersion(version)) {
            JPARSLogger.error("unsupported_service_version_in_the_request", new Object[] { version });
            throw new IllegalArgumentException();
        }

        PersistenceContext context = getPersistenceFactory().get(persistenceUnit, baseURI, version, initializationProperties);
        if (context == null) {
            JPARSLogger.error("jpars_could_not_find_persistence_context", new Object[] { persistenceUnit });
            throw JPARSException.persistenceContextCouldNotBeBootstrapped(persistenceUnit);
        }

        if ((entityType != null) && (context.getClass(entityType) == null)) {
            JPARSLogger.error(context.getSessionLog(), "jpars_could_not_find_class_in_persistence_unit", new Object[] { entityType, persistenceUnit });
            throw JPARSException.classOrClassDescriptorCouldNotBeFoundForEntity(entityType, persistenceUnit);
        }

        return context;
    }

    /**
     * Gets the relationship partner.
     *
     * @param matrixParams the matrix params
     * @param queryParams the query params
     * @return the relationship partner
     */
    protected String getRelationshipPartner(Map<String, String> matrixParams, Map<String, Object> queryParams) {
        String partner = null;
        // Fix for Bug 396791 - JPA-RS: partner should be treated as a query parameter
        // For backwards compatibility, we check both, matrix and query parameters.
        if ((queryParams != null) && (!queryParams.isEmpty())) {
            partner = (String) queryParams.get(QueryParameters.JPARS_RELATIONSHIP_PARTNER);
        }

        if (partner == null) {
            if ((matrixParams != null) && (!matrixParams.isEmpty())) {
                partner = matrixParams.get(MatrixParameters.JPARS_RELATIONSHIP_PARTNER);
            }
        }
        return partner;
    }

    /**
     * Marshall metadata.
     *
     * @param metadata the metadata
     * @param mediaType the media type
     * @return the string
     * @throws JAXBException the jAXB exception
     */
    protected String marshallMetadata(Object metadata, String mediaType) throws JAXBException {
        final Class<?>[] jaxbClasses = new Class[] { Link.class, Attribute.class, Descriptor.class, LinkTemplate.class, PersistenceUnit.class, Query.class, LinkList.class, QueryList.class,
                ResourceSchema.class, Property.class, Reference.class, LinkV2.class, MetadataCatalog.class, Resource.class, ItemLinks.class, ContextsCatalog.class };
        final JAXBContext context = (JAXBContext) JAXBContextFactory.createContext(jaxbClasses, null);
        final Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, Boolean.FALSE);
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, mediaType);
        marshaller.setProperty(MarshallerProperties.JSON_REDUCE_ANY_ARRAYS, true);

        final StringWriter writer = new StringWriter();
        marshaller.marshal(metadata, writer);
        return writer.toString();
    }

    @SuppressWarnings("unused")
    private static String getEncodedUri(String uri) {
        try {
            return URLEncoder.encode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO: Log it
            return uri;
        }
    }

    @SuppressWarnings("unused")
    private static String getDecodedUri(String uri) {
        try {
            return URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO: Log it
            return uri;
        }
    }

    protected void setRequestUniqueId() {
        DataStorage.set(DataStorage.REQUEST_ID, UUID.randomUUID().toString());
    }
}
