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
 *     09/23/2008-1.1 Guy Pelletier 
 *       - 241651: JPA 2.0 Access Type support
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Embeddable;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseField;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;

import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;

import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;

/**
 * An embedded relationship accessor. It may define all the same attributes
 * as an entity, therefore, it also must handle nesting embedded's to the nth
 * level. An embedded owning descriptor is a reference back to the actual
 * owning entity's descriptor where the first embedded was discovered.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class EmbeddedAccessor extends MappingAccessor {
    private List<AttributeOverrideMetadata> m_attributeOverrides;

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public EmbeddedAccessor() {
        super("<embedded>");
    }
    
    /**
     * INTERNAL:
     */
    protected EmbeddedAccessor(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    public EmbeddedAccessor(Annotation embedded, MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(embedded, accessibleObject, classAccessor);
        
        // Set the attribute overrides if some are present.
        m_attributeOverrides = new ArrayList<AttributeOverrideMetadata>();
        
        // Process the attribute overrides first.
        Annotation attributeOverrides = getAnnotation(AttributeOverrides.class);
        if (attributeOverrides != null) {
            for (Annotation attributeOverride : (Annotation[]) MetadataHelper.invokeMethod("value", attributeOverrides)) {
                m_attributeOverrides.add(new AttributeOverrideMetadata(attributeOverride, accessibleObject));
            }
        }
        
        // Process the single attribute override second.
        Annotation attributeOverride = getAnnotation(AttributeOverride.class);  
        if (attributeOverride != null) {
            m_attributeOverrides.add(new AttributeOverrideMetadata(attributeOverride, accessibleObject));
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<AttributeOverrideMetadata> getAttributeOverrides() {
        return m_attributeOverrides;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject) {
        super.initXMLObject(accessibleObject);
    
        // Initialize lists of ORMetadata objects.
        initXMLObjects(m_attributeOverrides, accessibleObject);
        
        // Set other column metadata that was not populated through OX.
        for (AttributeOverrideMetadata attributeOverride : m_attributeOverrides) {
            attributeOverride.getColumn().setAttributeName(attributeOverride.getName());
        }
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean isEmbedded() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process an embedded.
     */    
    public void process() {            
        // Process the embeddable class (and its accessors) first.
        processEmbeddableClass();
        
        // Create an aggregate mapping and do the rest of the work.
        AggregateObjectMapping mapping = new AggregateObjectMapping();
        mapping.setIsReadOnly(false);
        mapping.setIsNullAllowed(true);
        mapping.setReferenceClassName(getReferenceClassName());
        mapping.setAttributeName(getAttributeName());    
        
        // Will check for PROPERTY access
        setAccessorMethods(mapping);
        
        // Process attribute overrides.
        for (AttributeOverrideMetadata attributeOverride : m_attributeOverrides) {
            processAttributeOverride(mapping, attributeOverride);
        } 
       
        // Process association overrides (this is an annotation only thing).
        processAssociationOverrides(mapping);
        
        // process properties
        processProperties(mapping);
        
        // Process a @ReturnInsert and @ReturnUpdate (to log a warning message)
        processReturnInsertAndUpdate();
        
        // Store this descriptor metadata. It may be needed again later on to
        // look up a mappedBy attribute etc.
        getDescriptor().addEmbeddableDescriptor(getReferenceDescriptor());
        
        // Add the mapping to the descriptor and we are done.
        getDescriptor().addMapping(mapping);
    }
    
    /**
     * INTERNAL:
     * Process an AssociationOverride annotation for an embedded object, that 
     * is, an aggregate object mapping in EclipseLink. 
     * 
     * This functionality is not supported in XML for JPA. 
     * TODO: Perhaps we should add it to the EclipseLink XML schema.
     * 
     * Also this functionality is currently optional in the EJB 3.0 spec, but
     * since EclipseLink can handle it, it is implemented and assumes the user 
     * has properly configured its use since it will fail silently.
     */
    protected void processAssociationOverride(Object associationOverride, AggregateObjectMapping aggregateMapping) {
        MetadataDescriptor aggregateDescriptor = getReferenceDescriptor();
        
        // AssociationOverride.name(), the name of the attribute we want to
        // override.
        String name = (String) MetadataHelper.invokeMethod("name", associationOverride); 
        DatabaseMapping mapping = aggregateDescriptor.getMappingForAttributeName(name);
        
        if (mapping != null && mapping.isOneToOneMapping()) {
            int index = 0;
            
            for (Annotation joinColumn : (Annotation[]) MetadataHelper.invokeMethod("joinColumns", associationOverride)) { 
                // We can't change the mapping from the aggregate descriptor
                // so we have to add field name translations. This needs to be
                // tested since I am not entirely sure if this will actually
                // work.
                // In composite primary key case, how do we association the
                // foreign keys? Right now we assume the association overrides
                // are specified in the same order as the original joinColumns,
                // therefore in the same order the foreign keys were added to
                // the mapping.
                DatabaseField fkField = ((OneToOneMapping) mapping).getForeignKeyFields().elementAt(index++);
                aggregateMapping.addFieldNameTranslation((String) MetadataHelper.invokeMethod("name", joinColumn), fkField.getName());
            }   
        } else {
            // For now fail silently.
        }
    }
    
    /**
     * INTERNAL:
     * Process an AssociationOverrides annotation for an embedded object, that 
     * is, an aggregate object mapping in EclipseLink. 
     * 
     * It will also look for an AssociationOverride annotation.
     */
    protected void processAssociationOverrides(AggregateObjectMapping mapping) {
        // Look for an @AssociationOverrides.
        Annotation associationOverrides = getAnnotation(AssociationOverrides.class);
        if (associationOverrides != null) {
            for (Annotation associationOverride : (Annotation[]) MetadataHelper.invokeMethod("value", associationOverrides)) {
                processAssociationOverride(associationOverride, mapping);
            }
        }
        
        // Look for an @AssociationOverride.
        Annotation associationOverride = getAnnotation(AssociationOverride.class);    
        if (associationOverride != null) {
            processAssociationOverride(associationOverride, mapping);
        }
    }
    
    /**
     * INTERNAL:
     * Process an attribute override for an embedded object, that is, an 
     * aggregate object mapping in EclipseLink.
     */
    protected void processAttributeOverride(AggregateObjectMapping mapping, AttributeOverrideMetadata attributeOverride) {
        String attributeName = attributeOverride.getName();
        
        // Look for an aggregate mapping for the attribute name.
        DatabaseMapping aggregateMapping = getReferenceDescriptor().getMappingForAttributeName(attributeName);
        if (aggregateMapping == null) {
            throw ValidationException.invalidEmbeddableAttribute(getReferenceDescriptor().getJavaClass(), attributeName, getJavaClass(), mapping.getAttributeName());
        }
        
        // A sub-class (entity or mapped superclass) to a mapped superclass may 
        // override an embedded attribute override. This case will only hit 
        // since an entity may define attribute overrides to override in a 
        // mapped superclass. Within an embeddable there is no current way to 
        // specify attribute overrides (besides on the embedded mapping itself). 
        // So calling getDescriptor() is correct and calling 
        // getOwningDescriptor() is incorrect since it would allow attribute 
        // overrides defined on the owning entity to override nested embedded
        // attributes.
        if (getDescriptor().hasAttributeOverrideFor(attributeName)) {
            // Update the field on this metadata column. We do that so that  
            // an embedded id can associate the correct id fields. 
            attributeOverride.getColumn().setDatabaseField(getDescriptor().getAttributeOverrideFor(attributeName).getColumn().getDatabaseField());
        }
        
        // Now make sure we have a table set on the attribute override field.
        // If we need to default one, it should be the table from the owning
        // descriptor.
        DatabaseField overrideField = attributeOverride.getColumn().getDatabaseField();
        if (overrideField.getTableName().equals("")) {
            overrideField.setTable(getOwningDescriptor().getPrimaryTable());
        }

        DatabaseField aggregateField = aggregateMapping.getField();
        
        // If the override field is to an id field, we need to update the list
        // of primary keys on the owning descriptor. Embeddables can be shared
        // and different owners may want to override the attribute with a 
        // different column.
        if (getOwningDescriptor().isPrimaryKeyField(aggregateField)) {
            getOwningDescriptor().removePrimaryKeyField(aggregateField);
            getOwningDescriptor().addPrimaryKeyField(overrideField);
        }

        // Set the field name translation on the mapping.
        mapping.addFieldNameTranslation(overrideField.getQualifiedName(), aggregateField.getName());
    }
    
    /**
     * INTERNAL:
     * This method processes an embeddable class, if we have not processed it 
     * yet. The reason for lazy processing of embeddable class is because of 
     * rules  governing access-type of embeddable. See GlassFish issue #831 for 
     * more details.
     *
     * Be careful while changing the order of processing.
     */
    protected void processEmbeddableClass() {
        EmbeddableAccessor accessor = getProject().getEmbeddableAccessor(getReferenceClassName());

        if (accessor == null) {
            // Before throwing an exception we must make one final check for
            // an Embeddable annotation on the referenced class. At this point
            // we know the referenced class was not tagged as an embeddable
            // in a mapping file and was not included in the list of classes
            // for this persistence unit. Its inclusion therefore in this
            // persistence unit is through the use of an Embedded annotation
            // or an embedded element within a known entity. Therefore validate 
            // that the reference class does indeed have an Embeddable 
            // annotation.
            MetadataClass metadataClass = new MetadataClass(getReferenceClass());
            if (metadataClass.isAnnotationNotPresent(Embeddable.class)) {    
                throw ValidationException.invalidEmbeddedAttribute(getJavaClass(), getAttributeName(), getReferenceClass());
            } else {
                accessor = new EmbeddableAccessor(metadataClass.getAnnotation(Embeddable.class), getReferenceClass(), getProject());
                getProject().addEmbeddableAccessor(accessor);
            }
        } 
        
        if (accessor.isProcessed()) {
            // We have already processed this embeddable class. Let's validate 
            // that it is not used in entities with conflicting access type
            // when the embeddable doesn't have its own explicit setting. The
            // biggest mistake that could occur otherwise is that FIELD
            // processing 'could' yield a different mapping set then PROPERTY
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
            if (! accessor.hasAccess()) {
                // We inherited our access from our owning entity.
                if (! accessor.getDescriptor().getDefaultAccess().equals(getOwningDescriptor().getDefaultAccess())) {
                    throw ValidationException.conflictingAccessTypeForEmbeddable(accessor.getJavaClass(), accessor.usesPropertyAccess(), getOwningDescriptor().getJavaClass(), getOwningDescriptor().getClassAccessor().usesPropertyAccess());
                }
            }
        } else {
            // Need to set the owning descriptor on the embeddable class before 
            // we proceed any further in the processing.
            accessor.setOwningDescriptor(getOwningDescriptor());
            accessor.process();
            accessor.setIsProcessed();    
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setAttributeOverrides(List<AttributeOverrideMetadata> attributeOverrides) {
        m_attributeOverrides = attributeOverrides;
    }
}
