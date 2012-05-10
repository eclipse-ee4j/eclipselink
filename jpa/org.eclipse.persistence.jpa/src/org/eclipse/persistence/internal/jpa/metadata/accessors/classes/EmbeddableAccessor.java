/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
 *     04/09/2010-2.1 Guy Pelletier 
 *       - 307050: Add defaults for access methods of a VIRTUAL access type
 *     05/04/2010-2.1 Guy Pelletier 
 *       - 309373: Add parent class attribute to EclipseLink-ORM
 *     05/14/2010-2.1 Guy Pelletier 
 *       - 253083: Add support for dynamic persistence using ORM.xml/eclipselink-orm.xml
 *     06/01/2010-2.1 Guy Pelletier 
 *       - 315195: Add new property to avoid reading XML during the canonical model generation
 *     06/14/2010-2.2 Guy Pelletier 
 *       - 264417: Table generation is incorrect for JoinTables in AssociationOverrides
 *     07/05/2010-2.1.1 Guy Pelletier 
 *       - 317708: Exception thrown when using LAZY fetch on VIRTUAL mapping
 *     09/16/2010-2.2 Guy Pelletier 
 *       - 283028: Add support for letting an @Embeddable extend a @MappedSuperclass
 *     12/01/2010-2.2 Guy Pelletier 
 *       - 331234: xml-mapping-metadata-complete overriden by metadata-complete specification
 *     12/02/2010-2.2 Guy Pelletier 
 *       - 251554: ExcludeDefaultMapping annotation needed 
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.classes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ACCESS_FIELD;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_ACCESS_PROPERTY;

/**
 * INTERNAL:
 * An embeddable accessor.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be handled in the merge
 *   method. (merging is done at the accessor/mapping level)
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject  method.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */ 
public class EmbeddableAccessor extends ClassAccessor {
    // Embedding accessors is a map of those classes that embed this embeddable.
    // All embedding accessors are owning descriptors, but not vice versa.
    private Map<String, ClassAccessor> m_embeddingAccessors = new HashMap<String, ClassAccessor>();
    
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
     * INTERNAL:
     * Embedding accessors are those accessors that actually embed the 
     * embeddable class with an embedded mapping. We use this list to extract
     * and validate the access type of this embeddable when the embeddable
     * does not specify an explicit access type.
     */
    protected void addEmbeddingAccessor(ClassAccessor embeddingAccessor) {
       m_embeddingAccessors.put(embeddingAccessor.getJavaClassName(), embeddingAccessor);
    }
    
    /**
     * INTERNAL:
     */
    public void addEmbeddingAccessors(Map<String, ClassAccessor> embeddingAccessors) {
        m_embeddingAccessors.putAll(embeddingAccessors);
    }
    
    /**
     * INTERNAL:
     */
    public void addOwningDescriptor(MetadataDescriptor owningDescriptor) {
        getOwningDescriptors().add(owningDescriptor);
    }
    
    /**
     * INTERNAL:
     */
    public void addOwningDescriptors(List<MetadataDescriptor> owningDescriptors) {
        getOwningDescriptors().addAll(owningDescriptors);
    }
    
    /**
     * INTERNAL
     * Ensure any embeddable classes that are discovered during pre-process
     * are added to the project. The newly discovered embeddable accesors will
     * also be pre-processed now as well.
     */
    @Override
    protected void addPotentialEmbeddableAccessor(MetadataClass potentialEmbeddableClass, ClassAccessor embeddingAccessor) {
        if (potentialEmbeddableClass != null) {
            // Get embeddable accessor will add the embeddable to the  project 
            // if it is a valid embeddable. That is, if the class has an 
            // Embeddable annotation of the class is used as an IdClass for 
            // another entity within the persistence unit.
            EmbeddableAccessor embeddableAccessor = getProject().getEmbeddableAccessor(potentialEmbeddableClass, true);
        
            if (embeddableAccessor != null && ! embeddableAccessor.isPreProcessed()) {
                embeddableAccessor.addEmbeddingAccessor(embeddingAccessor);
                embeddableAccessor.addOwningDescriptors(getOwningDescriptors());
                embeddableAccessor.preProcess();
            }
        }
    }
    
    /**
     * INTERNAL:
     * Build a list of classes that are decorated with a MappedSuperclass
     * annotation or that are tagged as a mapped-superclass in an XML document.
     * 
     * This method will also do a couple other things as well since we are
     * traversing the parent classes:
     *  - Build a map of generic types specified and will be used to resolve 
     *    actual class types for mappings.
     *  - save mapped-superclass descriptors on the project for later use
     *    by the Metamodel API
     * 
     * We don't support embeddable inheritance yet. When that is added, this
     * method will need to change and in fact we may be able to re-use the
     * existing discover method from EntityAccessor (with minor tweaks).
     */
    protected void discoverMappedSuperclassesAndInheritanceParents(boolean addMappedSuperclassAccessors) {
        // Clear any previous discovery.
        clearMappedSuperclassesAndInheritanceParents();
        
        MetadataClass parentClass = getJavaClass().getSuperclass();
        List<String> genericTypes = getJavaClass().getGenericType();
        
        while (! parentClass.isObject()) {
            // Our parent might be a mapped superclass, check and add as needed.
            addPotentialMappedSuperclass(parentClass, addMappedSuperclassAccessors);
                
            // Resolve any generic types from the generic parent onto the 
            // current entity accessor.
            resolveGenericTypes(genericTypes, parentClass);
                
            // Grab the generic types from the parent class.
            genericTypes = parentClass.getGenericType();
                
            // Finally, get the next parent and keep processing ...
            parentClass = parentClass.getSuperclass();  
        }
    }
    
    /**
     * INTERNAL:
     */
    public Map<String, ClassAccessor> getEmbeddingAccessors() {
        return m_embeddingAccessors;
    }
    
    /**
     * INTERNAL:
     * So, here's the deal ... this method typically gets called when defaulting 
     * pk's name, primary table names etc. for various mappings. The problem 
     * however is that we go beyond the spec and allow more mappings to be 
     * specified on embeddable classes. For example, a M-M would default its 
     * join table and join columns using the info from its owning descriptor. 
     * The problem then is ... what to do when this embedabble has multiple 
     * owning descriptors? That is, is shared. Right now, their pk names from 
     * the owners better be same or mappings must be fully specified and not use 
     * any defaults. I think that is somewhat ok given we're going beyond the 
     * spec and TopLink doesn't even support it anyway???
     * 
     * So the stance is, we'll allow the extra mappings on embeddables that are 
     * not shared, however on shared cases there are restrictions. Users should 
     * use mapped superclasses when they have a need to share complex 
     * embeddables.
     * 
     * Or they can write customizers to modify their embeddable descriptors 
     * after initialize (after they have been cloned)
     * 
     * Future: the metadata processing 'could' set all necessary (per owning 
     * descriptor) metadata and have the descriptor initialize code handle it.
     * Metadata processing would process embeddable classes as it currently does 
     * for MappedSuperclasses. Clone them and process under each owning entity 
     * context. At descriptor initialize time, we would avoid cloning the 
     * aggregate descriptor and use the one metadata processing provided. 
     * Investigate further at a later date ...
     * 
     * Callers to this method are ...
     * BasicCollectionAccessor - processCollectionTable - defaults pk names from the owning descriptor.
     * RelationshipAccessor - processJoinTable - defaults the join table name and the source field name
     * OneToManyAccessor - processUnidirectionalOneToManyMapping - defaults the pk field and table.
     * MappingAccessor - processAssociationOverride and updatePrimaryKeyField.
     * ObjectAccessor - processId
     */
    @Override
    public MetadataDescriptor getOwningDescriptor() {
        // Return the first owning descriptor. In most cases this will be OK
        // since in most cases there is only one.
        return getOwningDescriptors().get(0);
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
     * The pre-process method is called during regular deployment and metadata
     * processing.
     * 
     * This method is called after each entity of the persistence unit has had 
     * an opportunity to pre-process itself first since we'll rely on owning 
     * entities for things like access type etc. The pre-process will run some 
     * validation. 
     * 
     * The order of processing is important, care must be taken if changes must 
     * be made. 
     */
    @Override
    public void preProcess() {
        // Perform the parent discovery process before processing any further.  
        discoverMappedSuperclassesAndInheritanceParents(true);
        
        // Process the correct access type before any other processing.
        processAccessType();
        
        // Process a virtual class specification after determining access type. 
        processVirtualClass();
        
        // Process the default access methods after determining access type.
        processAccessMethods();
        
        // Process our parents metadata after processing our own.
        super.preProcess();
    }
    
    /**
     * INTERNAL:
     * The pre-process for canonical model method is called (and only called) 
     * during the canonical model generation. The use of this pre-process allows
     * us to remove some items from the regular pre-process that do not apply
     * to the canonical model generation.
     * 
     * The order of processing is important, care must be taken if changes must 
     * be made.
     */
    @Override
    public void preProcessForCanonicalModel() {
        // Perform the parent discovery process before processing any further.  
        discoverMappedSuperclassesAndInheritanceParents(false);
        
        // Process our parents metadata after processing our own.
        super.preProcessForCanonicalModel();
    }
    
    /**
     * INTERNAL
     * Sub classes (Entity and Embeddable) must override this method to control 
     * the metadata that is processed for their context. 
     */
    @Override
    protected void preProcessMappedSuperclassMetadata(MappedSuperclassAccessor mappedSuperclass) {
        // Process the global converters.
        mappedSuperclass.processConverters();
        
        // Add the accessors and converters from this mapped superclass.
        mappedSuperclass.addAccessors();
    }
    
    /**
     * INTERNAL:
     * Process the metadata from this embeddable class.
     */
    @Override
    public void process() {
        // If a Cache annotation is present throw an exception.
        if (isAnnotationPresent(Cache.class)) {
            throw ValidationException.cacheNotSupportedWithEmbeddable(getJavaClass());
        } 
        
        // Process our parents metadata after processing our own.
        super.process();

        // Process the mapping accessors on this embeddable now.
        processMappingAccessors();
    }
    
    /**
     * INTERNAL:
     * For VIRTUAL access we need to look for default access methods that we 
     * need to use with our mapping attributes.
     */
    public void processAccessMethods() {
        // If we use virtual access and do not have any access methods
        // specified then get the default access methods from our owning
        // entity.
        if (hasAccessMethods()) {
            getDescriptor().setDefaultAccessMethods(getAccessMethods());
        } else {
            // The embeddable does not define default access methods. We 
            // need to look at our owning entities and 1) validate that
            // they all use the same default access methods and 2) grab
            // the default access methods from them.
            ClassAccessor embeddingAccessor = null;
            for (ClassAccessor currentEmbeddingAccessor : m_embeddingAccessors.values()) {
                if (embeddingAccessor == null) {
                    embeddingAccessor = currentEmbeddingAccessor;
                    continue;
                }
                
                if (! embeddingAccessor.getDescriptor().getDefaultAccessMethods().equals(currentEmbeddingAccessor.getDescriptor().getDefaultAccessMethods())) {
                    throw ValidationException.conflictingAccessMethodsForEmbeddable(getJavaClassName(), embeddingAccessor.getJavaClassName(), embeddingAccessor.getDescriptor().getDefaultAccessMethods(), currentEmbeddingAccessor.getJavaClassName(), currentEmbeddingAccessor.getDescriptor().getDefaultAccessMethods());
                }
            }

            // Set the default access methods on the descriptor and log a
            // message if there is an embedding accessor. Otherwise we'll
            // use the default set on our descriptor.
            if (embeddingAccessor != null) {
                getDescriptor().setDefaultAccessMethods(embeddingAccessor.getDescriptor().getDefaultAccessMethods());
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the access type of this embeddable. If this embeddable is not
     * embedded by at least one entity, it will not be processed. Therefore,
     * embedding accessors can not be empty at this point.
     */
    @Override
    protected void processAccessType() {
        // Validate that this embeddable is not used within entities with 
        // conflicting access types when the embeddable doesn't have its own 
        // explicit setting. When the embeddable is shared, the access type
        // must be the same across the board.
        if (! hasAccess()) {
            ClassAccessor embeddingAccessor = null;
            
            // The access type of the embeddable is determined from the class 
            // that is embedding it. If there are multiple embedding
            // accessors we will validate that there are no conflicting types.
            for (ClassAccessor currentEmbeddingAccessor : m_embeddingAccessors.values()) {
                if (embeddingAccessor == null) {
                    embeddingAccessor = currentEmbeddingAccessor;
                    continue;
                }
                
                if (! embeddingAccessor.getAccessType().equals(currentEmbeddingAccessor.getAccessType())) {
                    throw ValidationException.conflictingAccessTypeForEmbeddable(getJavaClassName(), embeddingAccessor.getJavaClassName(), embeddingAccessor.getAccessType(), currentEmbeddingAccessor.getJavaClassName(), currentEmbeddingAccessor.getAccessType());
                }
            }
            
            // Set the default access type on the descriptor and log a message
            // that we are defaulting the access type for this embeddable.
            if (embeddingAccessor == null) {
                // We don't have an owning entity (only possible during
                // canonical model generation) so look at the mapped 
                // superclasses. Ultimate default will be FIELD.
                String defaultAccessType = JPA_ACCESS_FIELD;
                for (MappedSuperclassAccessor mappedSuperclass : getMappedSuperclasses()) {
                    if (! mappedSuperclass.hasAccess()) {
                        if (mappedSuperclass.hasObjectRelationalFieldMappingAnnotationsDefined()) {
                            defaultAccessType = JPA_ACCESS_FIELD;
                        } else if (mappedSuperclass.hasObjectRelationalMethodMappingAnnotationsDefined()) {
                            defaultAccessType = JPA_ACCESS_PROPERTY;
                        }
                            
                        break;
                    }
                }

                getDescriptor().setDefaultAccess(defaultAccessType);
            } else {
                // Use the access type from the embedding accessor.
                getDescriptor().setDefaultAccess(embeddingAccessor.getAccessType());
            }
            
            getLogger().logConfigMessage(MetadataLogger.ACCESS_TYPE, getDescriptor().getDefaultAccess(), getJavaClass());
            
            getDescriptor().setAccessTypeOnClassDescriptor(getAccessType());
        } 
    }
    
    /**
     * INTERNAL
     * From an embeddable we need pair down what we process as things like
     * ID metadata does not apply. 
     */
    @Override
    protected void processMappedSuperclassMetadata(MappedSuperclassAccessor mappedSuperclass) {
        // Process the attribute override metadata.
        mappedSuperclass.processAttributeOverrides();
                    
        // Process the association override metadata.
        mappedSuperclass.processAssociationOverrides();
        
        // Process the change tracking metadata.
        mappedSuperclass.processChangeTracking();
        
        // Process the customizer metadata.
        mappedSuperclass.processCustomizer();
        
        // Process the copy policy metadata.
        mappedSuperclass.processCopyPolicy();
        
        // Process the property metadata.
        mappedSuperclass.processProperties();
    }
}
