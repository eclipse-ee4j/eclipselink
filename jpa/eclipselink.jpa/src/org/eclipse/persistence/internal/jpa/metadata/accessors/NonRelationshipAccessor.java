/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors;

import javax.persistence.SequenceGenerator;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * An relational accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class NonRelationshipAccessor extends MetadataAccessor {
	private SequenceGeneratorMetadata m_sequenceGenerator;
	private TableGeneratorMetadata m_tableGenerator;
	private XMLEntityMappings m_entityMappings;
	
    /**
     * INTERNAL:
     */
    public NonRelationshipAccessor() {}
    
	/**
     * INTERNAL:
     */
    public NonRelationshipAccessor(MetadataAccessibleObject accessibleObject, MetadataDescriptor descriptor, MetadataProject project) {
        super(accessibleObject, descriptor, project);
    }
    
    /**
     * INTERNAL:
     */
    public NonRelationshipAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
    }
    
    /**
     * INTERNAL:
     */
    public XMLEntityMappings getEntityMappings() {
    	return m_entityMappings;
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
     * INTERNAL:
	 * Process a SequenceGenerator annotation into a common metadata sequence 
	 * generator.
     */
    protected void processSequenceGenerator() {
    	// Process the xml defined sequence generator first.
    	if (m_sequenceGenerator != null) {
    		// Ask the common processor to process what we found.
    		getEntityMappings().processSequenceGenerator(m_sequenceGenerator, getJavaClassName());
    	}
        
        // Process the annotation defined sequence generator second.        
        SequenceGenerator sequenceGenerator = getAnnotation(SequenceGenerator.class);
        
        if (sequenceGenerator != null) {
            // Ask the common processor to process what we found.
        	getProject().processSequenceGenerator(new SequenceGeneratorMetadata(sequenceGenerator, getJavaClassName()));
        }
    }
    
    /**
     * INTERNAL:
	 * Process a TableGenerator annotation into a common metadata table 
	 * generator.
     */
    protected void processTableGenerator() {
        // Process the xml defined table generator first.
    	if (m_tableGenerator != null) {
    		// Ask the common processor to process what we found.
    		getEntityMappings().processTableGenerator(m_tableGenerator, getDescriptor().getXMLCatalog(), getDescriptor().getXMLSchema(), getJavaClassName());
    	}
        
        // Process the annotation defined table generator second.
        TableGenerator tableGenerator = getAnnotation(TableGenerator.class);
        
        if (tableGenerator != null) {
            // Ask the common processor to process what we found.
            getProject().processTableGenerator(new TableGeneratorMetadata(tableGenerator, getJavaClassName()), getDescriptor().getXMLCatalog(), getDescriptor().getXMLSchema());
        }
    } 

    /**
     * INTERNAL:
     */
    public void setEntityMappings(XMLEntityMappings entityMappings) {
    	m_entityMappings = entityMappings;
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
