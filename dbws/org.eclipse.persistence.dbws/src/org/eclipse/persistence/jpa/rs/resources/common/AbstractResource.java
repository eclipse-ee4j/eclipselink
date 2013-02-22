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
package org.eclipse.persistence.jpa.rs.resources.common;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;

import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Attribute;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Descriptor;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Link;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.LinkTemplate;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.PersistenceUnit;
import org.eclipse.persistence.internal.jpa.rs.metadata.model.Query;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jpa.rs.MatrixParameters;
import org.eclipse.persistence.jpa.rs.PersistenceContext;
import org.eclipse.persistence.jpa.rs.PersistenceContextFactory;
import org.eclipse.persistence.jpa.rs.PersistenceContextFactoryProvider;
import org.eclipse.persistence.jpa.rs.QueryParameters;
import org.eclipse.persistence.jpa.rs.util.JPARSLogger;
import org.eclipse.persistence.jpa.rs.util.list.LinkList;
import org.eclipse.persistence.jpa.rs.util.list.QueryList;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * @author gonural
 *
 */

public abstract class AbstractResource {
    public static final String SERVICE_VERSION_FORMAT = "v\\d\\.\\d"; 
    protected PersistenceContextFactory factory;

    public static final String SERVICE_VERSION_1_0 = "v1.0"; 

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
    
    protected PersistenceContextFactory buildPersistenceContextFactory(ClassLoader loader){
        ServiceLoader<PersistenceContextFactoryProvider> contextFactoryLoader = ServiceLoader.load(PersistenceContextFactoryProvider.class, loader);

        for (PersistenceContextFactoryProvider provider: contextFactoryLoader){
            PersistenceContextFactory factory = provider.getPersistenceContextFactory(null);
            if (factory != null){
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
     * 
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
     *
     */
    protected static Map<String, Object> getQueryParameters(UriInfo info) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        for (String key : info.getQueryParameters().keySet()) {
            queryParameters.put(key, info.getQueryParameters().getFirst(key));
        }
        return queryParameters;
    }
    
    /**
     * Creates the shell jaxb element list.
     *
     * @param reportItems the report items
     * @return the list
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List<JAXBElement> createShellJAXBElementList(List<ReportItem> reportItems, Object record) {
        List<JAXBElement> jaxbElements = new ArrayList<JAXBElement>(reportItems.size());
        if ((reportItems != null) && (reportItems.size() > 0)) {
            for (int index = 0; index < reportItems.size(); index++) {
                ReportItem reportItem = reportItems.get(index);
                Object reportItemValue = record;
                if (record instanceof Object[]) {
                    reportItemValue = ((Object[]) record)[index];
                }
                Class reportItemValueType = null;
                if (reportItemValue != null) {
                    reportItemValueType = reportItemValue.getClass();
                }
                if (reportItemValueType == null) {
                    // try other paths to determine the type of the report item 
                    DatabaseMapping dbMapping = reportItem.getMapping();
                    if (dbMapping != null) {
                        reportItemValueType = dbMapping.getAttributeClassification();
                    } else {
                        ClassDescriptor desc = reportItem.getDescriptor();
                        if (desc != null) {
                            reportItemValueType = desc.getJavaClass();
                        } 
                    }
                }

                // so, we couldn't determine the type of the report item, stop here... 
                if (reportItemValueType == null) {
                    return null;
                }
                
                JAXBElement element = new JAXBElement(new QName(reportItem.getName()), reportItemValueType, reportItemValue);
                jaxbElements.add(reportItem.getResultIndex(), element);
            }
        }
        return jaxbElements;
    }
    
    
    protected static boolean isValidVersion(String version) {
        if ((version == null) || (SERVICE_VERSION_1_0.equals(version))) {
            return true;
        }
        return false;
    }
    
    protected PersistenceContext getPersistenceContext(String persistenceUnit, URI baseURI, String version, Map<String, Object> initializationProperties) {
        if (!isValidVersion(version)) {
            JPARSLogger.fine("unsupported_service_version_in_the_request", new Object[] { version });
            throw new IllegalArgumentException();
        }

        return getPersistenceFactory().get(persistenceUnit, baseURI, version, initializationProperties);
    }
    
    protected String getRelationshipPartner(Map<String, String> matrixParams, Map<String, Object> queryParams) {
        String partner = null;
        // Fix for Bug 396791 - JPA-RS: partner should be treated as a query parameter
        //
        // For backwards compatibility, we check both, matrix and query parameters.
        if ((queryParams != null) && (!queryParams.isEmpty())) {
            partner = (String) queryParams.get(QueryParameters.JPARS_RELATIONSHIP_PARTNER);
        }

        if (partner == null) {
            if ((matrixParams != null) && (!matrixParams.isEmpty())) {
                partner = (String) matrixParams.get(MatrixParameters.JPARS_RELATIONSHIP_PARTNER);
            }
        }
        return partner;
    }
    
    protected String marshallMetadata(Object metadata, String mediaType) throws JAXBException {
        Class<?>[] jaxbClasses = new Class[] { Link.class, Attribute.class, Descriptor.class, LinkTemplate.class, PersistenceUnit.class, Query.class, LinkList.class, QueryList.class };
        JAXBContext context = (JAXBContext) JAXBContextFactory.createContext(jaxbClasses, null);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, Boolean.FALSE);
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, mediaType);
        marshaller.setProperty(MarshallerProperties.JSON_REDUCE_ANY_ARRAYS, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(metadata, writer);
        return writer.toString();
    }

}