/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
 *     01/28/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 1)
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     02/26/2009-2.0 Guy Pelletier 
 *       - 264001: dot notation for mapped-by and order-by
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/03/2009-2.0 Guy Pelletier
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/24/2009-2.0 Guy Pelletier 
 *       - 270011: JPA 2.0 MappedById support
 *     06/25/2009-2.0 Michael O'Brien 
 *       - 266912: change MappedSuperclass handling in stage2 to pre process accessors
 *          in support of the custom descriptors holding mappings required by the Metamodel 
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

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
    public EmbeddableAccessor(MetadataAnnotation annotation, MetadataClass cls, MetadataProject project) {
        super(annotation, cls, project);
    }
    
    /**
     * INTERNAL
     * Ensure any embeddable classes that are discovered during pre-process
     * are added to the project. The newly discovered embeddable accesors will
     * also be pre-processed now as well.
     */
    @Override
    protected void addPotentialEmbeddableAccessor(MetadataClass potentialEmbeddableClass) {
        if (potentialEmbeddableClass != null) {
            // Get embeddable accessor will add the embeddable to the 
            // project if it is a valid embeddable. That is, if one the class
            // has an Embeddable annotation of the class is used as an IdClass
            // for another entity within the persistence unit.
            EmbeddableAccessor embeddableAccessor = getProject().getEmbeddableAccessor(potentialEmbeddableClass, true);
        
            if (embeddableAccessor != null && ! embeddableAccessor.isPreProcessed()) {
                embeddableAccessor.setOwningDescriptor(getOwningDescriptor());
                embeddableAccessor.preProcess();
            }
        }
    }
    
    /** 
     * INTERNAL:
     * Return true if this accessor represents an embeddable accessor.
     */
    @Override
    public boolean isEmbeddableAccessor() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process the items of interest on an embeddable class.
     */
    @Override
    public void preProcess() {
        setIsPreProcessed();
        
        // Step 1 - process the embeddable specifics, like access type, 
        // metadata complete etc. which we need before proceeding any further.
        processEmbeddable();

        // Step 2 - Add the accessors and converters on this embeddable.
        addAccessors();
        addConverters();
    }
    

    /**
     * INTERNAL:
     */
    @Override
    public void preProcessForCanonicalModel() {        
        preProcess();
    }
    
    /**
     * INTERNAL:
     * Process the items of interest on an embeddable class.
     */
    @Override
    public void process() {
        setIsProcessed();
        
        // If a Cache annotation is present throw an exception.
        if (isAnnotationPresent(Cache.class)) {
            throw ValidationException.cacheNotSupportedWithEmbeddable(getJavaClass());
        } 
    
        // Process the customizer metadata.
        processCustomizer();
        
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
     * This method processes an embeddable class, if we have not processed it 
     * yet. Be careful while changing the order of processing.
     * <p>
     * MappedSuperclass descriptors have relaxed constraints.
     */
    public void process(MetadataDescriptor owningDescriptor) {
        if (isProcessed()) {
            // We have already processed this embeddable class. Let's validate 
            // that it is not used in entities with conflicting access type
            // when the embeddable doesn't have its own explicit setting. The
            // biggest mistake that could occur otherwise is that FIELD
            // processing 'could' yield a different mapping set than PROPERTY
            // processing would. Do we really care? If both access types
            // yielded the same mappings then the only difference would be
            // how they are accessed and well ... does it really matter at this
            // point? The only way to know if they would yield different
            // mappings would be by processing the class for each access type
            // and comparing the yield or some other code to manually inspect
            // the class. I think this error should be removed since the spec
            // states: 
            //  "Embedded objects belong strictly to their owning entity, and 
            //   are not sharable across persistent entities. Attempting to 
            //   share an embedded object across entities has undefined 
            //   semantics."
            // I think we should assume the users know what they are are doing
            // in this case (that is, if they opt to share an embeddable).
            if (! hasAccess()) {
                // We inherited our access from our owning entity.
                if (! getDescriptor().getDefaultAccess().equals(owningDescriptor.getDefaultAccess())) {
                    // 266912: relax restrictions when either the accessor on this or the owning descriptor is a MappedSuperclass                    
                    if(!getDescriptor().isMappedSuperclass() && !owningDescriptor.isMappedSuperclass()) {
                            throw ValidationException.conflictingAccessTypeForEmbeddable(getJavaClass(), usesPropertyAccess(), owningDescriptor.getJavaClass(), owningDescriptor.getClassAccessor().usesPropertyAccess());
                    }
                }
            }
            
            // TODO: We also need to do more processing here for shared
            // embeddables. Primary key settings etc...
        } else {
            // Need to set the owning descriptor on the embeddable class before 
            // we proceed any further in the processing.
            setOwningDescriptor(owningDescriptor);
            process(); 
        }
    }
    
    /**
     * INTERNAL:
     * Process the access type of this embeddable.
     */
    protected void processAccessType() {
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
