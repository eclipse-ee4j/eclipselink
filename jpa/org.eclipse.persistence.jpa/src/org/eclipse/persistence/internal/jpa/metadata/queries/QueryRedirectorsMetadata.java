/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 *     03/28/2011-2.3 Guy Pelletier 
 *       - 341152: From XML cache interceptor and query redirector metadata don't support package specification
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import org.eclipse.persistence.descriptors.ClassDescriptor;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * Object to hold onto Default Redirector metadata.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Gordon Yorke
 * @since EclipseLink 1.0
 */
public class QueryRedirectorsMetadata extends ORMetadata {
    protected MetadataClass defaultQueryRedirector;
    protected MetadataClass defaultReadAllQueryRedirector;
    protected MetadataClass defaultReadObjectQueryRedirector;
    protected MetadataClass defaultReportQueryRedirector;
    protected MetadataClass defaultUpdateObjectQueryRedirector;
    protected MetadataClass defaultInsertObjectQueryRedirector;
    protected MetadataClass defaultDeleteObjectQueryRedirector;
    
    protected String defaultQueryRedirectorName;
    protected String defaultReadAllQueryRedirectorName;
    protected String defaultReadObjectQueryRedirectorName;
    protected String defaultReportQueryRedirectorName;
    protected String defaultUpdateObjectQueryRedirectorName;
    protected String defaultInsertObjectQueryRedirectorName;
    protected String defaultDeleteObjectQueryRedirectorName;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public QueryRedirectorsMetadata() {
        super("<query-redirectors>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public QueryRedirectorsMetadata(MetadataAnnotation redirectors, MetadataAccessor accessor) {
        super(redirectors, accessor);
        
        defaultQueryRedirector = getMetadataClass((String) redirectors.getAttribute("allQueries"));
        defaultReadAllQueryRedirector = getMetadataClass((String) redirectors.getAttribute("readAll"));
        defaultReadObjectQueryRedirector = getMetadataClass((String) redirectors.getAttribute("readObject"));
        defaultInsertObjectQueryRedirector = getMetadataClass((String) redirectors.getAttribute("insert"));
        defaultDeleteObjectQueryRedirector = getMetadataClass((String) redirectors.getAttribute("delete"));
        defaultUpdateObjectQueryRedirector = getMetadataClass((String) redirectors.getAttribute("update"));
        defaultReportQueryRedirector = getMetadataClass((String) redirectors.getAttribute("report"));
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof QueryRedirectorsMetadata) {
            QueryRedirectorsMetadata queryRedirectors = (QueryRedirectorsMetadata) objectToCompare;
            
            if (! valuesMatch(defaultQueryRedirectorName, queryRedirectors.getDefaultQueryRedirectorName())) {
                return false;
            }
            
            if (! valuesMatch(defaultReadAllQueryRedirectorName, queryRedirectors.getDefaultReadAllQueryRedirectorName())) {
                return false;
            }
            
            if (! valuesMatch(defaultReadObjectQueryRedirectorName, queryRedirectors.getDefaultReadObjectQueryRedirectorName())) {
                return false;
            }
            
            if (! valuesMatch(defaultReportQueryRedirectorName, queryRedirectors.getDefaultReportQueryRedirectorName())) {
                return false;
            }
            
            if (! valuesMatch(defaultUpdateObjectQueryRedirectorName, queryRedirectors.getDefaultUpdateObjectQueryRedirectorName())) {
                return false;
            }
            
            if (! valuesMatch(defaultInsertObjectQueryRedirectorName, queryRedirectors.getDefaultInsertObjectQueryRedirectorName())) {
                return false;
            }
            
            return valuesMatch(defaultDeleteObjectQueryRedirectorName, queryRedirectors.getDefaultDeleteObjectQueryRedirectorName());
        }
        
        return false;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDefaultDeleteObjectQueryRedirectorName() {
        return defaultDeleteObjectQueryRedirectorName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDefaultInsertObjectQueryRedirectorName() {
        return defaultInsertObjectQueryRedirectorName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDefaultQueryRedirectorName() {
        return defaultQueryRedirectorName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDefaultReadAllQueryRedirectorName() {
        return defaultReadAllQueryRedirectorName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDefaultReadObjectQueryRedirectorName() {
        return defaultReadObjectQueryRedirectorName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDefaultReportQueryRedirectorName() {
        return defaultReportQueryRedirectorName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDefaultUpdateObjectQueryRedirectorName() {
        return defaultUpdateObjectQueryRedirectorName;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        // Initialize our classes names to actual classes (taking a package
        // specification into account.
        defaultQueryRedirector = initXMLClassName(defaultQueryRedirectorName);
        defaultReadAllQueryRedirector = initXMLClassName(defaultReadAllQueryRedirectorName);
        defaultReadObjectQueryRedirector = initXMLClassName(defaultReadObjectQueryRedirectorName);
        defaultReportQueryRedirector = initXMLClassName(defaultReportQueryRedirectorName);
        defaultUpdateObjectQueryRedirector = initXMLClassName(defaultUpdateObjectQueryRedirectorName);
        defaultInsertObjectQueryRedirector = initXMLClassName(defaultInsertObjectQueryRedirectorName);
        defaultDeleteObjectQueryRedirector = initXMLClassName(defaultDeleteObjectQueryRedirectorName);
    }
    
    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor, MetadataClass javaClass) {
        // Set the cache flag on the metadata Descriptor.
        descriptor.setHasDefaultRedirectors();
        
        // Process the cache metadata.
        ClassDescriptor classDescriptor = descriptor.getClassDescriptor();
        
        if (!defaultQueryRedirector.isVoid()) classDescriptor.setDefaultQueryRedirectorClassName(defaultQueryRedirector.getName());
        if (!defaultReadAllQueryRedirector.isVoid()) classDescriptor.setDefaultReadAllQueryRedirectorClassName(defaultReadAllQueryRedirector.getName());
        if (!defaultReadObjectQueryRedirector.isVoid()) classDescriptor.setDefaultReadObjectQueryRedirectorClassName(defaultReadObjectQueryRedirector.getName());
        if (!defaultReportQueryRedirector.isVoid()) classDescriptor.setDefaultReportQueryRedirectorClassName(defaultReportQueryRedirector.getName());
        if (!defaultInsertObjectQueryRedirector.isVoid()) classDescriptor.setDefaultInsertObjectQueryRedirectorClassName(defaultInsertObjectQueryRedirector.getName());
        if (!defaultUpdateObjectQueryRedirector.isVoid()) classDescriptor.setDefaultUpdateObjectQueryRedirectorClassName(defaultUpdateObjectQueryRedirector.getName());
        if (!defaultDeleteObjectQueryRedirector.isVoid()) classDescriptor.setDefaultDeleteObjectQueryRedirectorClassName(defaultDeleteObjectQueryRedirector.getName());
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDefaultDeleteObjectQueryRedirectorName(String defaultDeleteObjectQueryRedirectorName) {
        this.defaultDeleteObjectQueryRedirectorName = defaultDeleteObjectQueryRedirectorName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDefaultInsertObjectQueryRedirectorName(String defaultInsertObjectQueryRedirectorName) {
        this.defaultInsertObjectQueryRedirectorName = defaultInsertObjectQueryRedirectorName;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDefaultQueryRedirectorName(String defaultQueryRedirectorName) {
        this.defaultQueryRedirectorName = defaultQueryRedirectorName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDefaultReadAllQueryRedirectorName(String defaultReadAllQueryRedirectorName) {
        this.defaultReadAllQueryRedirectorName = defaultReadAllQueryRedirectorName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDefaultReadObjectQueryRedirectorName(String defaultReadObjectQueryRedirectorName) {
        this.defaultReadObjectQueryRedirectorName = defaultReadObjectQueryRedirectorName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDefaultReportQueryRedirectorName(String defaultReportQueryRedirectorName) {
        this.defaultReportQueryRedirectorName = defaultReportQueryRedirectorName;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDefaultUpdateObjectQueryRedirectorName(String defaultUpdateObjectQueryRedirectorName) {
        this.defaultUpdateObjectQueryRedirectorName = defaultUpdateObjectQueryRedirectorName;
    }
}
