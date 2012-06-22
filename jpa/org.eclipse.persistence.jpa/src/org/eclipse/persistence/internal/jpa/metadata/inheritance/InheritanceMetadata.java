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
 *     Guy Pelletier (Oracle), February 28, 2007 
 *        - New file introduced for bug 217880.
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 *     12/18/2009-2.1 Guy Pelletier 
 *       - 211323: Add class extractor support to the EclipseLink-ORM.XML Schema
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.inheritance;

import javax.persistence.InheritanceType;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.descriptors.TablePerClassPolicy;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * Object to represent inheritance metadata. The processing of this metadata
 * to its related class descriptors should be performed by this class.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class InheritanceMetadata extends ORMetadata {
    private String m_strategy;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public InheritanceMetadata() {
        super("<inheritance>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public InheritanceMetadata(MetadataAnnotation inheritance, MetadataAccessor accessor) {
        super(inheritance, accessor);
        
        if (inheritance != null) {
            m_strategy = (String)inheritance.getAttribute("strategy");
        }
    }
    
    /**
     * INTERNAL:
     * Set the class extractor class name on the inheritance policy.
     */
    protected void addClassExtractor(MetadataDescriptor descriptor, EntityAccessor accessor) { 
        descriptor.getClassDescriptor().getInheritancePolicy().setClassExtractorName(accessor.processClassExtractor());
    }
    
    /**
     * INTERNAL:
     * Recursive method.
     */
    protected void addClassIndicator(MetadataDescriptor descriptor, EntityAccessor accessor) {
        if (descriptor.isInheritanceSubclass()) {
            addClassIndicator(descriptor.getInheritanceRootDescriptor(), accessor);
        } else {
            // Get the discriminator value from the accessor (this will do any
            // defaulting if necessary and log a message). If the discriminator 
            // value is null then presumably the inheritance subclass is 
            // abstract and should be not be added to the class name indicator 
            // list.

            String discriminatorValue = accessor.processDiscriminatorValue();
            if (discriminatorValue != null) {
                if (descriptor.getClassDescriptor().getInheritancePolicy().getClassIndicatorField().getType() == Integer.class){
                    try {
                        descriptor.getClassDescriptor().getInheritancePolicy().addClassNameIndicator(accessor.getJavaClassName(), Integer.valueOf(discriminatorValue));
                    } catch (NumberFormatException exc){
                        accessor.getLogger().logWarningMessage(MetadataLogger.WARNING_INCORRECT_DISCRIMINATOR_FORMAT, accessor.getJavaClassName(), discriminatorValue);
                    }
                } else {
                    descriptor.getClassDescriptor().getInheritancePolicy().addClassNameIndicator(accessor.getJavaClassName(), discriminatorValue);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void addClassIndicatorField(MetadataDescriptor descriptor, EntityAccessor accessor) {
        descriptor.getClassDescriptor().getInheritancePolicy().setClassIndicatorField(accessor.processDiscriminatorColumn());
    }
    
    /**
     * INTERNAL:
     * Recursive method to traverse table per class inheritance hierarchy and
     * grab all the 'inherited' accessors for subclasses of the hierarchy.
     * 
     * What we know: 
     * - All parent classes will already have been processed. Inheritance
     *   hierarchies are processed top->down.
     * - Always go through the given descriptors pointer to its class accessor,
     *   as we can not rely on the reloaded accessors for inheritance checks,
     *   mapped superclasses etc. Use the descriptors provided.
     * - When adding accessors from superclasses to an inheritance subclasses 
     *   descriptor, they must be reloaded/cloned and cannot be shared. 
     *   Otherwise the processing of those accessor will only be performed once
     *   by their 'real' owning entity accessor. 
     */
    public void addTablePerClassParentMappings(MetadataDescriptor startingDescriptor, MetadataDescriptor realDescriptor) {
        EntityAccessor reloadedParentEntity = null;
        MetadataDescriptor realParentDescriptor = null;
        
        // If we are an inheritance subclass, recursively call up to the root 
        // entity so that we can grab a copy of all our inherited mapping 
        // accessors. Copies of our parent accessors are done by reloading the 
        // parent entities through OX (if they were originally loaded from XML). 
        // This is our way of cloning. The reloaded accessors are rebuilt using 
        // the startingDescriptor context, that is where we want to add the 
        // accessors.
        if (realDescriptor.isInheritanceSubclass() && realDescriptor.getInheritanceRootDescriptor().usesTablePerClassInheritanceStrategy()) {
            realParentDescriptor = realDescriptor.getInheritanceParentDescriptor();
            reloadedParentEntity = reloadEntity((EntityAccessor) realParentDescriptor.getClassAccessor(), startingDescriptor);
            addTablePerClassParentMappings(startingDescriptor, realParentDescriptor);
        }
        
        // If we are the starting entity, the processing of our mapped 
        // superclass and our accessors will be done when we process our 
        // immediate accessors. Also, our immediate mapped superclasses will 
        // have other metadata for us to process (and not just the addition of 
        // accessors). See EntityAccesor process() and processClassMetadata().
        if (reloadedParentEntity != null) {
            // Be sure to reload the mapped superclass from the 'real' entity 
            // accessor which has already discovered the list.
            EntityAccessor realParentEntityAccessor = (EntityAccessor) realParentDescriptor.getClassAccessor();
            
            for (MappedSuperclassAccessor mappedSuperclass : realParentEntityAccessor.getMappedSuperclasses()) {
                // Reload the mapped superclass and add its accessors.
                reloadMappedSuperclass(mappedSuperclass, startingDescriptor).addAccessors();
            }
            
            // Add the mapping accessors from the reloaded entity.
            reloadedParentEntity.addAccessors();
        }
    }
    
    /**
     * INTERNAL:
     * For merging and overriding to work properly, all ORMetadata must be able 
     * to compare themselves for metadata equality.
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof InheritanceMetadata) {
            return valuesMatch(m_strategy, ((InheritanceMetadata) objectToCompare).getStrategy());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getStrategy() {
        return m_strategy;
    }
    
    /**
     * INTERNAL:
     * The process method method will be called with the descriptor from
     * every entity in the hierarchy.
     */
    public void process(MetadataDescriptor descriptor) {
        EntityAccessor accessor = (EntityAccessor) descriptor.getClassAccessor();
        
        // Set the correct inheritance policy.
        if (m_strategy != null && m_strategy.equals(InheritanceType.TABLE_PER_CLASS.name())) {
            setTablePerClassInheritancePolicy(descriptor);
        } else {
            setInheritancePolicy(descriptor);
        }
        
        // Process an inheritance subclass.
        if (descriptor.isInheritanceSubclass()) {
            MetadataDescriptor rootDescriptor = descriptor.getInheritanceRootDescriptor();
            EntityAccessor rootAccessor = (EntityAccessor) rootDescriptor.getClassAccessor();
                 
            if (rootDescriptor.usesTablePerClassInheritanceStrategy()) {
                MetadataDescriptor parentDescriptor = descriptor.getInheritanceParentDescriptor();
                descriptor.getClassDescriptor().getTablePerClassPolicy().addParentDescriptor(parentDescriptor.getClassDescriptor());
                parentDescriptor.getClassDescriptor().getTablePerClassPolicy().addChildDescriptor(descriptor.getClassDescriptor());
            } else {
                // Set the parent class on the inheritance policy.
                descriptor.getClassDescriptor().getInheritancePolicy().setParentClassName(descriptor.getInheritanceParentDescriptor().getJavaClassName());                
            }
            
            // If we have inheritance defined then we are a root parent and the 
            // strategy should be changing meaning we have double the metadata
            // to process.
            // Note: if the strategy does not change, then we ignore the
            // inheritance hierarchy and continue as if we were a simple 
            // inheritance subclass.
            if (accessor.hasInheritance() && ! equals(rootAccessor.getInheritance())) {
                // Process the inheritance root metadata.
                processInheritanceRoot(descriptor, accessor);
            } else {
                // Process the inheritance sub class metadata.
                processInheritanceSubclass(descriptor, accessor, rootAccessor);
            }
            
            // If the root descriptor has an id class, we need to set the same 
            // id class on our descriptor.
            if (rootDescriptor.hasCompositePrimaryKey()) {
                descriptor.setPKClass(rootDescriptor.getPKClass());
            }
        } else {
            // Process the inheritance root metadata.
            processInheritanceRoot(descriptor, accessor);
        }
    }
    
    /**
     * INTERNAL:
     * Process the inheritance metadata of a root class.
     */
    protected void processInheritanceRoot(MetadataDescriptor descriptor, EntityAccessor accessor) {
        // If we are an inheritance subclass, then the strategy must be changing 
        // and we therefore have a little more work to do.
        if (descriptor.isInheritanceSubclass()) {
            if (descriptor.getInheritanceRootDescriptor().usesTablePerClassInheritanceStrategy()) {
                // If our parent used a TABLE_PER_CLASS strategy then we need to 
                // add the table per class mappings from our parents, including 
                // their mapped superclasses, to our list of accessors.
                addTablePerClassParentMappings(descriptor, descriptor);
            } else {
                // Indicators are used with all strategies except for 
                // TABLE_PER_CLASS. However, if the parent used anything but 
                // TPC we need to process primary key join columns since the 
                // strategies are changing and we need to link up the classes.
                accessor.processInheritancePrimaryKeyJoinColumns();    
            }
        }
        
        // Indicators are used with all strategies except for TABLE_PER_CLASS.
        if (! usesTablePerClassStrategy()) {
            if (accessor.hasClassExtractor()) {
                addClassExtractor(descriptor, accessor);
            } else {
                addClassIndicatorField(descriptor, accessor);
                addClassIndicator(descriptor, accessor);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Process the inheritance metadata of a sub class.
     */
    protected void processInheritanceSubclass(MetadataDescriptor descriptor, EntityAccessor accessor, EntityAccessor rootAccessor) {
        if (usesTablePerClassStrategy()) {
            // Go through our parents, including their mapped superclasses, 
            // and add their accessors to our list of accessors.
            addTablePerClassParentMappings(descriptor, descriptor);
        } else {
            // If the root accessor doesn't have a class extractor then add
            // the class indicator.
            if (! rootAccessor.hasClassExtractor()) {
                addClassIndicator(rootAccessor.getDescriptor(), accessor);
            }
            
            // Process join columns if necessary.
            if (rootAccessor.getInheritance().usesJoinedStrategy()) {
                accessor.processInheritancePrimaryKeyJoinColumns();
            }
        }
    }
    
    /**
     * INTERNAL:
     * Recursive method.
     */
    protected void setInheritancePolicy(MetadataDescriptor descriptor) {
        if (m_strategy == null && ! descriptor.isInheritanceSubclass()) {
            // TODO: Log a defaulting message
        }
        
        descriptor.getClassDescriptor().setInheritancePolicy(new InheritancePolicy(descriptor.getClassDescriptor()));
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setStrategy(String strategy) {
        m_strategy = strategy;
    }
    
    /**
     * INTERNAL:
     */
    protected void setTablePerClassInheritancePolicy(MetadataDescriptor descriptor) {
        descriptor.getClassDescriptor().setTablePerClassPolicy(new TablePerClassPolicy(descriptor.getClassDescriptor()));
    }
    
    /**
     * INTERNAL:
     */
    public boolean usesJoinedStrategy() {
        return m_strategy != null && m_strategy.equals(InheritanceType.JOINED.name()); 
    }
    
    /**
     * INTERNAL:
     */
    public boolean usesSingleTableStrategy() {
        return m_strategy == null || m_strategy.equals(InheritanceType.SINGLE_TABLE.name()); 
    }
    
    /**
     * INTERNAL:
     */
    public boolean usesTablePerClassStrategy() {
        return m_strategy != null && m_strategy.equals(InheritanceType.TABLE_PER_CLASS.name()); 
    }
}
    
