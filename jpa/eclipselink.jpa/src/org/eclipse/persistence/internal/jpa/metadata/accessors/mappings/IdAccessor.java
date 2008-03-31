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

import javax.persistence.GeneratedValue;
import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.exceptions.ValidationException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.GeneratedValueMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;

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
	private SequenceGeneratorMetadata m_sequenceGenerator;
	private TableGeneratorMetadata m_tableGenerator;
	
    /**
     * INTERNAL:
     */
    public IdAccessor() {}
    
    /**
     * INTERNAL:
     */
    public IdAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        
        // Set the generated value if one is present.
        if (isAnnotationPresent(GeneratedValue.class)) {
            m_generatedValue = new GeneratedValueMetadata(getAnnotation(GeneratedValue.class));
        }
        
        // Set the sequence generator if one is present.        
        if (isAnnotationPresent(SequenceGenerator.class)) {
            m_sequenceGenerator = new SequenceGeneratorMetadata(getAnnotation(SequenceGenerator.class), getJavaClassName());
        }
        
        // Set the sequence generator if one is present.        
        if (isAnnotationPresent(TableGenerator.class)) {
            m_tableGenerator = new TableGeneratorMetadata(getAnnotation(TableGenerator.class), getJavaClassName());
        }
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
     * Used for OX mapping.
     */
    public SequenceGeneratorMetadata getSequenceGenerator() {
        return m_sequenceGenerator;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public TableGeneratorMetadata getTableGenerator() {
        return m_tableGenerator;
    }
    
    /**
     * INTERNAL: (Override from MetadataAccessor)
     */
    public void init(MetadataAccessibleObject accessibleObject, ClassAccessor accessor) {
        super.init(accessibleObject, accessor);
        
        // Initialize the sequence generator with metadata that couldn't be
        // loaded through OX.
        if (m_sequenceGenerator != null) {
            m_sequenceGenerator.setLocation(getJavaClassName());
        }
        
        // Initialize the table generator with metadata that couldn't be
        // loaded through OX.
        if (m_tableGenerator != null) {
            m_tableGenerator.setLocation(getJavaClassName());
        }
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

        // Add the table generator to the project if one is set.
        if (m_tableGenerator != null) {
            getProject().addTableGenerator(m_tableGenerator, getDescriptor().getXMLCatalog(), getDescriptor().getXMLSchema());
        }

        // Add the sequence generator to the project if one is set.
        if (m_sequenceGenerator != null) {
            getProject().addSequenceGenerator(m_sequenceGenerator);
        }
    }

    /**
     * INTERNAL:
     * Process the generated value metadata.
     */
    protected void processGeneratedValue() {
        if (m_generatedValue != null) {
            // Set the sequence number field on the descriptor.		
            DatabaseField existingSequenceNumberField = getDescriptor().getSequenceNumberField();

            if (existingSequenceNumberField == null) {
                getDescriptor().setSequenceNumberField(getField());
                getProject().addGeneratedValue(m_generatedValue, getJavaClass());
            } else {
                throw ValidationException.onlyOneGeneratedValueIsAllowed(getJavaClass(), existingSequenceNumberField.getQualifiedName(), getField().getQualifiedName());
            }
        }
    }

	/**
     * INTERNAL:
     * Used for OX mapping.
     */
	public void setGeneratedValue(GeneratedValueMetadata value) {
		m_generatedValue = value;
	}
	
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setSequenceGenerator(SequenceGeneratorMetadata sequenceGenerator) {
        m_sequenceGenerator = sequenceGenerator;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTableGenerator(TableGeneratorMetadata tableGenerator) {
        m_tableGenerator = tableGenerator;
    }
}
