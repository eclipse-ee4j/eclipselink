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
 *     Guy Pelletier (Oracle), February 28, 2007 
 *        - New file introduced for bug 217880.
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.     
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.inheritance;

import javax.persistence.InheritanceType;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.descriptors.TablePerClassPolicy;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

/**
 * Object to represent inheritance metadata. The processing of this metadata
 * to its related class descriptors should be performed by this class.
 * 
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public class InheritanceMetadata extends ORMetadata {
    private String m_strategy;
    
    /**
     * INTERNAL:
     */
    public InheritanceMetadata() {
        super("<inheritance>");
    }
    
    /**
     * INTERNAL:
     */
    public InheritanceMetadata(MetadataAnnotation inheritance, MetadataAccessibleObject accessibleObject) {
        super(inheritance, accessibleObject);
        
        if (inheritance != null) {
            m_strategy = (String)inheritance.getAttribute("strategy");
        }
    }
    
    /**
     * INTERNAL:
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
            // strategy should be changing meaning we have double the work to do. 
            // Note: if the strategy does not change, then we ignore the inheritance 
            // hierarchy and continue as if we were a simple inheritance subclass.
            if (accessor.hasInheritance() && ! equals(rootAccessor.getInheritance())) {
                // If our parent was a table per class strategy then we need
                // to add the table per class mappings.
                if (rootDescriptor.usesTablePerClassInheritanceStrategy()) {
                    // Go through our parents (including their mapped superclasses 
                    // and add their accessors to our list of accessors.
                    addTablePerClassParentMappings(descriptor, descriptor);
                } else {
                    if (! usesTablePerClassStrategy()) {
                        // We are either a JOINED or SINGLE_TABLE strategy and we
                        // need to process our specific inheritance metadata.
                        addClassIndicatorField(descriptor, accessor);
                        addClassIndicator(rootDescriptor, accessor);
                    }
                    
                    // The strategies are changing so must process some 
                    // join columns to link up the classes.
                    accessor.processInheritancePrimaryKeyJoinColumns();
                }
            } else {
                // We are a simple inheritance subclass.
                if (usesTablePerClassStrategy()) {
                    // Go through our parents (including their mapped superclasses 
                    // and add their accessors to our list of accessors.
                    addTablePerClassParentMappings(descriptor, descriptor);
                } else {
                    // We have metadata we need to set on our root parent.
                    addClassIndicator(rootDescriptor, accessor);
                    
                    // Process join columns if necessary.
                    if (rootAccessor.getInheritance().usesJoinedStrategy()) {
                        accessor.processInheritancePrimaryKeyJoinColumns();
                    }
                }
            }
            
            // If the root descriptor has an id class, we need to set the same 
            // id class on our descriptor.
            if (descriptor.getInheritanceRootDescriptor().hasCompositePrimaryKey()) {
                descriptor.setPKClass(descriptor.getInheritanceRootDescriptor().getPKClass());
            }
        } else {
            // Since Inheritance hierarchies are processed from the top most
            // root down, only the top most roots will go through this code.
            if (! usesTablePerClassStrategy()) {
                // We are either a JOINED or SINGLE_TABLE strategy and we
                // need to process our specific inheritance metadata.
                addClassIndicatorField(descriptor, accessor);
                addClassIndicator(descriptor, accessor);
            }  
        }
    }
    
    /**
     * INTERNAL:
     * Recursive method.
     */
    private void addClassIndicator(MetadataDescriptor rootDescriptor, EntityAccessor accessor) {
        if (rootDescriptor.isInheritanceSubclass()) {
            addClassIndicator(rootDescriptor.getInheritanceRootDescriptor(), accessor);
        } else {
            // Get the discriminator value from the accessor (this will do any
            // defaulting if necessary and log a message). If the discriminator 
            // value is null then presumably the inheritance subclass is 
            // abstract and should be not be added to the class name indicator 
            // list.

            String discriminatorValue = accessor.getDiscriminatorValueOrNull();
            if (discriminatorValue != null) {
                if (rootDescriptor.getClassDescriptor().getInheritancePolicy().getClassIndicatorField().getType() == Integer.class){
                    try{
                        Integer integerDiscriminator = new Integer(discriminatorValue);
                        rootDescriptor.getClassDescriptor().getInheritancePolicy().addClassNameIndicator(accessor.getJavaClassName(), integerDiscriminator);
                        return;
                    } catch (NumberFormatException exc){
                        accessor.getLogger().logWarningMessage(MetadataLogger.WARNING_INCORRECT_DISCRIMINATOR_FORMAT, accessor.getJavaClassName(), discriminatorValue);
                    }
                }
                rootDescriptor.getClassDescriptor().getInheritancePolicy().addClassNameIndicator(accessor.getJavaClassName(), discriminatorValue);
            }
        }
    }
    
    /**
     * INTERNAL:
     */
    private void addClassIndicatorField(MetadataDescriptor descriptor, EntityAccessor accessor) {
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
     * Recursive method.
     */
    private void setInheritancePolicy(MetadataDescriptor descriptor) {
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
    private void setTablePerClassInheritancePolicy(MetadataDescriptor descriptor) {
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
    
