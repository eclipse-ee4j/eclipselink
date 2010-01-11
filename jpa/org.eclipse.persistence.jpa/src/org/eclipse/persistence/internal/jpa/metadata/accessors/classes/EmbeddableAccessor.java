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
 *     05/23/2008-1.0M8 Guy Pelletier 
 *       - 211330: Add attributes-complete support to the EclipseLink-ORM.XML Schema
 *     07/15/2008-1.0.1 Guy Pelletier 
 *       - 240679: MappedSuperclass Id not picked when on get() method accessor
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 *     10/01/2008-1.1 Guy Pelletier 
 *       - 249329: To remain JPA 1.0 compliant, any new JPA 2.0 annotations should be referenced by name
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import java.lang.annotation.Annotation;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;

/**
 * INTERNAL:
 * An embeddable accessor.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */ 
public class EmbeddableAccessor extends ClassAccessor {
    /**
     * INTERNAL:
     */
    public EmbeddableAccessor() {
        super("<embeddable>");
    }
    
    /**
     * INTERNAL:
     */
    public EmbeddableAccessor(Annotation annotation, Class cls, MetadataProject project) {
        super(annotation, cls, project);
    }
    
    /**
     * INTERNAL:
     * Process the items of interest on an embeddable class.
     */
    public void process() {
        // If a Cache annotation is present throw an exception.
        if (isAnnotationPresent(Cache.class)) {
            throw ValidationException.cacheNotSupportedWithEmbeddable(getJavaClass());
        } 
        
        // Process the Embeddable metadata first. Need to ensure we determine 
        // the access, metadata complete and exclude default mappings before we 
        // process further.
        processEmbeddable();
    
        // Process the customizer metadata.
        processCustomizer();
        
        // Process the converter metadata.
        processConverters();   
        
        // Process the copy policy metadata.
        processCopyPolicy();
        
        // Process the change tracking metadata.
        processChangeTracking();
        
        // Process the properties metadata.
        processProperties();

        // Process the accessors on this embeddable.
        processAccessors();
    }
    
    /**
     * INTERNAL:
     * Process the access type of this embeddable.
     */
    public void processAccessType() {
        // Set the default access type on the descriptor and log a message
        // to the user if we are defaulting the access type for this 
        // embeddable to that default.
        String owningClassAccessorsAccess = getOwningDescriptor().getClassAccessor().getAccessType();
        getDescriptor().setDefaultAccess(owningClassAccessorsAccess);
        
        if (getAccess() == null) {
            getLogger().logConfigMessage(MetadataLogger.ACCESS_TYPE, owningClassAccessorsAccess, getJavaClass());
        }
    }
    
    /**
     * INTERNAL:
     * Process the embeddable metadata.
     */
    protected void processEmbeddable() {
        // Process the access type first.
        processAccessType();
        
        // Set a metadata complete flag if specified.
        if (getMetadataComplete() != null) {
            getDescriptor().setIgnoreAnnotations(isMetadataComplete());
        } 
        
        // Set an exclude default mappings flag if specified.
        if (getExcludeDefaultMappings() != null) {
            getDescriptor().setIgnoreDefaultMappings(excludeDefaultMappings());
        } 
    }
}
