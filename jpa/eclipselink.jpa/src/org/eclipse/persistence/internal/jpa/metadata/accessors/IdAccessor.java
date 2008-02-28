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
package org.eclipse.persistence.internal.jpa.metadata.accessors;

import javax.persistence.GeneratedValue;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.GeneratedValueMetadata;

import org.eclipse.persistence.internal.helper.DatabaseField;

/**
 * A relational accessor. A Basic annotation may or may not be present on the
 * accessible object.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class IdAccessor extends BasicAccessor {
	private GeneratedValueMetadata m_generatedValue;
	
    /**
     * INTERNAL:
     */
    public IdAccessor() {}
    
    /**
     * INTERNAL:
     */
    public IdAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
	public GeneratedValueMetadata getGeneratedValue() {
		return m_generatedValue;
	}
	
    /**
     * INTERNAL:
     * Process an id accessor.
     */
    public void process() {
    	// This will initialize the m_field variable.
    	super.process();
    	
    	String attributeName = getAttributeName();

        if (getDescriptor().hasEmbeddedIdAttribute()) {
        	// We found both an Id and an EmbeddedId, throw an exception.
        	throw ValidationException.embeddedIdAndIdAnnotationFound(getJavaClass(), getDescriptor().getEmbeddedIdAttributeName(), attributeName);
        }

        // If this entity has a pk class, we need to validate our ids. 
        getDescriptor().validatePKClassId(attributeName, getReferenceClass());

        // Store the Id attribute name. Used with validation and OrderBy.
        getDescriptor().addIdAttributeName(attributeName);

        // Add the primary key field to the descriptor.            
        getDescriptor().addPrimaryKeyField(getField());

        // Process the generated value for this id.
        processGeneratedValue();

        // Process a table generator.
        processTableGenerator();

        // Process a sequence generator.
        processSequenceGenerator();
    }

    /**
     * INTERNAL:
     * Process a GeneratedValue annotation.
     */
    protected void processGeneratedValue() {
        if (m_generatedValue == null) {
        	// Look for an annotation.
            if (isAnnotationPresent(GeneratedValue.class)) {
                processGeneratedValue(new GeneratedValueMetadata(getAnnotation(GeneratedValue.class)), getField());
            }
        } else {
            // Ask the common processor to process what we found.
            processGeneratedValue(m_generatedValue, getField());
        }
    }

    /**
     * INTERNAL:
     * Process a GeneratedValue metadata.
     */
    protected void processGeneratedValue(GeneratedValueMetadata generatedValue, DatabaseField sequenceNumberField) {
        // Set the sequence number field on the descriptor.		
        DatabaseField existingSequenceNumberField = getDescriptor().getSequenceNumberField();

        if (existingSequenceNumberField == null) {
            getDescriptor().setSequenceNumberField(sequenceNumberField);
            getProject().addGeneratedValue(generatedValue, getJavaClass());
        } else {
        	throw ValidationException.onlyOneGeneratedValueIsAllowed(getJavaClass(), existingSequenceNumberField.getQualifiedName(), sequenceNumberField.getQualifiedName());
        }
    }

	/**
     * INTERNAL:
     * Used for OX mapping.
     */
	public void setGeneratedValue(GeneratedValueMetadata value) {
		m_generatedValue = value;
	}
}
