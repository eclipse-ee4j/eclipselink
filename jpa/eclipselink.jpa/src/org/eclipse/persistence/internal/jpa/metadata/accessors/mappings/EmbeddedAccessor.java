/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import static org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor.AccessType.MIXED;
import static org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor.AccessType.PROPERTY;
import static org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor.AccessType.FIELD;
import static org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor.AccessType.UNDEFINED;

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
import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.internal.jpa.metadata.columns.AttributeOverrideMetadata;

import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;

/**
 * An embedded relationship accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class EmbeddedAccessor extends MetadataAccessor {
    enum AccessType {FIELD, PROPERTY, UNDEFINED, MIXED};
    
    private List<AttributeOverrideMetadata> m_attributeOverrides;

    /**
     * INTERNAL:
     */
    public EmbeddedAccessor() {}
    
    /**
     * INTERNAL:
     */
    public EmbeddedAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        
        // Set the attribute overrides if some are present.
        m_attributeOverrides = new ArrayList<AttributeOverrideMetadata>();
        
        // Process the attribute overrides first.
        Annotation attributeOverrides = getAnnotation(AttributeOverrides.class);
        if (attributeOverrides != null) {
            for (Annotation attributeOverride : (Annotation[]) MetadataHelper.invokeMethod("value", attributeOverrides)) {
                m_attributeOverrides.add(new AttributeOverrideMetadata(attributeOverride, getJavaClassName()));
            }
        }
        
        // Process the single attribute override second.
        Annotation attributeOverride = getAnnotation(AttributeOverride.class);  
        if (attributeOverride != null) {
            m_attributeOverrides.add(new AttributeOverrideMetadata(attributeOverride, getJavaClassName()));
        }
    }
    
    /**
     * INTERNAL:
     * This method computes access-type based on placement of annotations.
     */
    protected AccessType computeAccessTypeFromAnnotation(MetadataDescriptor descriptor) {
        boolean fieldAccess = MetadataHelper.havePersistenceAnnotationsDefined(MetadataHelper.getFields(descriptor.getJavaClass()));
        boolean propertyAccess = MetadataHelper.havePersistenceAnnotationsDefined(MetadataHelper.getMethods(descriptor.getJavaClass()));
        
        if (fieldAccess && propertyAccess) {
        	return MIXED;
        } else if (fieldAccess) {
        	return FIELD;
        } else if (propertyAccess) {
        	return PROPERTY;
        } else {
        	return UNDEFINED;
        }
    }
    
    /**
     * INTERNAL:
     * This method is responsible for determining the access-type for an
     * Embeddable class represented by emDescr that is passed to
     * this method. This method should *not* be called more than once as this is
     * quite expensive. Now the rules:
     *
     * Rule 1: In the *absence* of metadata in embeddable class, access-type of
     * an embeddable is determined by the access-type of the enclosing entity.
     *
     * Rule 2: In the presence of metadata in embeddable class, access-type of
     * an embeddable is determined using that metadata. This allows sharing
     * of the embeddable in entities with conflicting access-types.
     *
     * Rule 3: It is an error to use a *metadata-less* embeddable class in
     * entities with conflicting access-types as that might result in
     * different database mapping for the same embeddable class.
     *
     * Rule 4: It is an error if metadata-complete == false, and
     * metadata is present *both* in annotations and XML, and
     * access-type as determined by each of them is *not* same.
     *
     * Rule 5: It is an error if *both* fields and properties of an embeddable
     * class are annotated and metadata-complete == false.
     *
     * @param emDesc descriptor for the Embeddable class
     */
    protected AccessType determineAccessTypeOfEmbedded(MetadataDescriptor descriptor) {
    	AccessType accessType = UNDEFINED;
    	
    	// 1 - get the owning entities' access type.
        AccessType entityAccessType = getDescriptor().usesPropertyAccess() ? PROPERTY : FIELD;
        
        // 2 - get the access type as specified in XML.
        AccessType xmlAccessType = descriptor.getXMLAccess() == null ? UNDEFINED : AccessType.valueOf(descriptor.getXMLAccess());

        if (descriptor.ignoreAnnotations()) {
            // metadata-complete is true, then use XML access-type if defined,
            // else use enclosing entity's access-type.
            accessType = xmlAccessType == UNDEFINED ? entityAccessType : xmlAccessType;
        } else {
        	// Let's look at access via location of annotations.
            AccessType annotationAccessType = computeAccessTypeFromAnnotation(descriptor);
            
            if (annotationAccessType == UNDEFINED && xmlAccessType == UNDEFINED) {
                // metadata is absent, so we use enclosing entity's access-type
                accessType = entityAccessType;
            } else if (xmlAccessType == UNDEFINED && annotationAccessType != UNDEFINED) {
                // annotation is present in embeddable class
                accessType = annotationAccessType;

                if (accessType == MIXED) {
                	throw ValidationException.bothFieldsAndPropertiesAnnotated(descriptor.getJavaClass());
                }
            } else if (annotationAccessType == UNDEFINED && xmlAccessType != UNDEFINED) {
                // access is defined using XML for embeddable class
                accessType = xmlAccessType;
            } else if (annotationAccessType == xmlAccessType) {
                // Annotations are present as well as access is defined using 
            	// XML and they are same. Use it!
                accessType = annotationAccessType;
            } else {
                // Annotations are present as well as access is defined using 
            	// XML and they are different, throw an exception.
            	throw ValidationException.incorrectOverridingOfAccessType(descriptor.getJavaClass(), xmlAccessType.toString(), annotationAccessType.toString());
            }
        }

        return accessType;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
	public List<AttributeOverrideMetadata> getAttributeOverrides() {
		return m_attributeOverrides;
	}
	
    /**
     * INTERNAL: (Override from MetadataAccessor)
     */
    public void init(MetadataAccessibleObject accessibleObject, ClassAccessor accessor) {
        super.init(accessibleObject, accessor);
        
        // Set other column metadata that was not populated through OX.
        for (AttributeOverrideMetadata attributeOverride : m_attributeOverrides) {
            attributeOverride.getColumn().setAttributeName(attributeOverride.getName());
        }
    }
    
    /**
     * INTERNAL:
     */
	public boolean isEmbedded() {
        return true;
    }
    
    /**
     * INTERNAL:
     * 
     * This method is used to decide if a class metadata or not.
     */
    protected boolean isMetadataPresent(MetadataDescriptor descriptor) {
        AccessType annotAccessType = computeAccessTypeFromAnnotation(descriptor);
        AccessType xmlAccessType = (descriptor.getXMLAccess() == null) ? UNDEFINED : AccessType.valueOf(descriptor.getXMLAccess());

        return annotAccessType != UNDEFINED || xmlAccessType != UNDEFINED;
    }
    
    /**
     * INTERNAL: (Overridden in EmbeddedIdAccessor)
     * Process an embedded.
     */    
    public void process() {
        // Process a @ReturnInsert and @ReturnUpdate (to log a warning message)
        processReturnInsertAndUpdate();
        
        // Tell the Embeddable class to process itself
        MetadataDescriptor referenceDescriptor = processEmbeddableClass();
        
        // Store this descriptor metadata. It may be needed again later on to
        // look up a mappedBy attribute.
        getDescriptor().addAggregateDescriptor(referenceDescriptor);
        
        if (getDescriptor().hasMappingForAttributeName(getAttributeName())) {
            // XML/Annotation merging. XML wins, ignore annotations.
            getLogger().logWarningMessage(MetadataLogger.IGNORE_MAPPING, getDescriptor(), this);
        } else {
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
        
            // Add the mapping to the descriptor and we are done.
            getDescriptor().addMapping(mapping);
        }
    }
    
    /**
     * INTERNAL:
     * 
     * Process an @AssociationOverride for an embedded object, that is, an 
     * aggregate object mapping in TopLink. 
     * 
     * This functionality is not supported in XML, hence why this method is 
     * defined here instead of on MetadataProcessor.
     * 
     * Also this functionality is currently optional in the EJB 3.0 spec, but
     * since TopLink can handle it, it is implemented and assumes the user has
     * properly configured its use since it will fail silently.
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
                // tested since I am not entirely sure if this will acutally
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
     * INTERNAL: (Overridden in EmbeddedIdAccessor)
     * Process an attribute override for an embedded object, that is, an 
     * aggregate object mapping in EclipseLink.
	 */
	protected void processAttributeOverride(AggregateObjectMapping mapping, AttributeOverrideMetadata attributeOverride) {
        String attributeName = attributeOverride.getName();
        
        // Set the attribute name on the aggregate.
        DatabaseMapping aggregateMapping = getReferenceDescriptor().getMappingForAttributeName(attributeName);
        
        if (aggregateMapping == null) {
        	throw ValidationException.invalidEmbeddableAttribute(getReferenceDescriptor().getJavaClass(), attributeName, getJavaClass(), mapping.getAttributeName());
        }
        
        // A sub-class to a mapped superclass may override an embedded attribute 
        // override.
        if (getDescriptor().hasAttributeOverrideFor(attributeName)) {
            // Update the field on this metadata column. We do that so that
            // an embedded id can associate the correct id fields.
            attributeOverride.getColumn().setDatabaseField(getDescriptor().getAttributeOverrideFor(attributeName).getColumn().getDatabaseField());
        } 
        
        mapping.addFieldNameTranslation(attributeOverride.getColumn().getDatabaseField().getQualifiedName(), aggregateMapping.getField().getName());
	}
    
    /**
     * INTERNAL: (Overridden in EmbeddedIdAccessor)
     * This method processes an embeddable class, if we have not processed it 
     * yet. The reason for lazy processing of embeddable class is because of 
     * rules  governing access-type of embeddable. See GlassFish issue #831 for 
     * more details.
     *
     * Be careful while changing order of processing.
     */
    protected MetadataDescriptor processEmbeddableClass() {
        EmbeddableAccessor embeddableAccessor = getProject().getEmbeddableAccessor(getReferenceClassName());

        if (embeddableAccessor == null) {
        	// Before throwing an exception we must make one final check for
        	// an Embeddable annotation on the referenced class. At this point
        	// we know the referenced class was not tagged as an embeddable
        	// in a mapping file and was not included in the list of classes
        	// for this persistence unit. Its inclusion therefore in this
        	// persistence unit is through the use of an Embedded annotation
        	// or an embedded element within a known entity. Therefore validate 
        	// that the reference class does indeed have an Embeddable 
        	// annotation.
        	if (isAnnotationNotPresent(Embeddable.class, getReferenceClass())) {
        		throw ValidationException.invalidEmbeddedAttribute(getJavaClass(), getAttributeName(), getReferenceClass());
        	} else {
        		embeddableAccessor = new EmbeddableAccessor(getReferenceClass(), getProject());
        		getProject().addEmbeddableAccessor(embeddableAccessor);
        	}
        } 
        
        MetadataDescriptor embeddableDescriptor = embeddableAccessor.getDescriptor();
        	
        if (! embeddableAccessor.isProcessed()) {
        	AccessType accessType = determineAccessTypeOfEmbedded(embeddableDescriptor);
            embeddableDescriptor.setUsesPropertyAccess(accessType == PROPERTY ? true : false);
            embeddableAccessor.process();
            embeddableAccessor.setIsProcessed();	
        }
        	
        // We have already processed this embeddable class. Let's validate 
        // that it is not used in entities with conflicting access type. 
        // Conflicting access-type is not allowed when there is no metadata 
        // in the embeddable class.
        if (! isMetadataPresent(embeddableDescriptor)) {
        	if (embeddableDescriptor.usesPropertyAccess() != getDescriptor().usesPropertyAccess()) {
        		throw ValidationException.conflictingAccessTypeForEmbeddable(embeddableDescriptor.getJavaClass());
            }
        }
        
        return embeddableDescriptor;
    }

	/**
	 * INTERNAL:
	 * Used for OX mapping.
	 */
	public void setAttributeOverrides(List<AttributeOverrideMetadata> attributeOverrides) {
		m_attributeOverrides = attributeOverrides;
	}
}
