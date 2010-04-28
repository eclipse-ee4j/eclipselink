/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.queries;

import org.eclipse.persistence.descriptors.ClassDescriptor;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

/**
 * Object to hold onto Default Redirector metadata.
 * 
 * @author Gordon Yorke
 * @since EclipseLink 1.0
 */
public class QueryRedirectorsMetadata extends ORMetadata {
    // Note: Any metadata mapped from XML to this class must be compared in the equals method.

    protected String defaultQueryRedirector;
    protected String defaultReadAllQueryRedirector;
    protected String defaultReadObjectQueryRedirector;
    protected String defaultReportQueryRedirector;
    protected String defaultUpdateObjectQueryRedirector;
    protected String defaultInsertObjectQueryRedirector;
    protected String defaultDeleteObjectQueryRedirector;

    /**
     * INTERNAL:
     */
    public QueryRedirectorsMetadata() {
        super("<query-redirectors>");
    }
    
    /**
     * INTERNAL:
     */
    public QueryRedirectorsMetadata(MetadataAnnotation redirectors, MetadataAccessibleObject accessibleObject) {
        super(redirectors, accessibleObject);
        
        defaultQueryRedirector = (String)redirectors.getAttribute("allQueries");
        defaultReadAllQueryRedirector = (String)redirectors.getAttribute("readAll");
        defaultReadObjectQueryRedirector = (String)redirectors.getAttribute("readObject");
        defaultInsertObjectQueryRedirector = (String)redirectors.getAttribute("insert");
        defaultDeleteObjectQueryRedirector = (String)redirectors.getAttribute("delete");
        defaultUpdateObjectQueryRedirector = (String)redirectors.getAttribute("update");
        defaultReportQueryRedirector = (String)redirectors.getAttribute("report");
    }

    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof QueryRedirectorsMetadata) {
            QueryRedirectorsMetadata queryRedirectors = (QueryRedirectorsMetadata) objectToCompare;
            
            if (! valuesMatch(defaultQueryRedirector, queryRedirectors.getDefaultQueryRedirector())) {
                return false;
            }
            
            if (! valuesMatch(defaultReadAllQueryRedirector, queryRedirectors.defaultReadAllQueryRedirector)) {
                return false;
            }
            
            if (! valuesMatch(defaultReadObjectQueryRedirector, queryRedirectors.getDefaultReadObjectQueryRedirector())) {
                return false;
            }
            
            if (! valuesMatch(defaultReportQueryRedirector, queryRedirectors.getDefaultReportQueryRedirector())) {
                return false;
            }
            
            if (! valuesMatch(defaultUpdateObjectQueryRedirector, queryRedirectors.getDefaultUpdateObjectQueryRedirector())) {
                return false;
            }
            
            if (! valuesMatch(defaultInsertObjectQueryRedirector, queryRedirectors.getDefaultInsertObjectQueryRedirector())) {
                return false;
            }
            
            return valuesMatch(defaultDeleteObjectQueryRedirector, queryRedirectors.getDefaultDeleteObjectQueryRedirector());
        }
        
        return false;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDefaultDeleteObjectQueryRedirector() {
        return defaultDeleteObjectQueryRedirector;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDefaultInsertObjectQueryRedirector() {
        return defaultInsertObjectQueryRedirector;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDefaultQueryRedirector() {
        return defaultQueryRedirector;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDefaultReadAllQueryRedirector() {
        return defaultReadAllQueryRedirector;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDefaultReadObjectQueryRedirector() {
        return defaultReadObjectQueryRedirector;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDefaultReportQueryRedirector() {
        return defaultReportQueryRedirector;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getDefaultUpdateObjectQueryRedirector() {
        return defaultUpdateObjectQueryRedirector;
    }
    
    /**
     * INTERNAL:
     */
    public void process(MetadataDescriptor descriptor, MetadataClass javaClass) {
        // Set the cache flag on the metadata Descriptor.
        descriptor.setHasDefaultRedirectors();
        
        // Process the cache metadata.
        ClassDescriptor classDescriptor = descriptor.getClassDescriptor();
        
        if (!defaultQueryRedirector.equals(void.class.getName())) classDescriptor.setDefaultQueryRedirectorClassName(defaultQueryRedirector);
        if (!defaultReadAllQueryRedirector.equals(void.class.getName()))classDescriptor.setDefaultReadAllQueryRedirectorClassName(defaultReadAllQueryRedirector);
        if (!defaultReadObjectQueryRedirector.equals(void.class.getName()))classDescriptor.setDefaultReadObjectQueryRedirectorClassName(defaultReadObjectQueryRedirector);
        if (!defaultReportQueryRedirector.equals(void.class.getName()))classDescriptor.setDefaultReportQueryRedirectorClassName(defaultReportQueryRedirector);
        if (!defaultInsertObjectQueryRedirector.equals(void.class.getName()))classDescriptor.setDefaultInsertObjectQueryRedirectorClassName(defaultInsertObjectQueryRedirector);
        if (!defaultUpdateObjectQueryRedirector.equals(void.class.getName()))classDescriptor.setDefaultUpdateObjectQueryRedirectorClassName(defaultUpdateObjectQueryRedirector);
        if (!defaultDeleteObjectQueryRedirector.equals(void.class.getName()))classDescriptor.setDefaultDeleteObjectQueryRedirectorClassName(defaultDeleteObjectQueryRedirector);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDefaultDeleteObjectQueryRedirector(String defaultDeleteObjectQueryRedirector) {
        this.defaultDeleteObjectQueryRedirector = defaultDeleteObjectQueryRedirector;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDefaultInsertObjectQueryRedirector(String defaultInsertObjectQueryRedirector) {
        this.defaultInsertObjectQueryRedirector = defaultInsertObjectQueryRedirector;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDefaultQueryRedirector(String defaultQueryRedirector) {
        this.defaultQueryRedirector = defaultQueryRedirector;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDefaultReadAllQueryRedirector(String defaultReadAllQueryRedirector) {
        this.defaultReadAllQueryRedirector = defaultReadAllQueryRedirector;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDefaultReadObjectQueryRedirector(String defaultReadObjectQueryRedirector) {
        this.defaultReadObjectQueryRedirector = defaultReadObjectQueryRedirector;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDefaultReportQueryRedirector(String defaultReportQueryRedirector) {
        this.defaultReportQueryRedirector = defaultReportQueryRedirector;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setDefaultUpdateObjectQueryRedirector(String defaultUpdateObjectQueryRedirector) {
        this.defaultUpdateObjectQueryRedirector = defaultUpdateObjectQueryRedirector;
    }
}
