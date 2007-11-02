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

import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;

import org.eclipse.persistence.internal.jpa.metadata.MetadataConstants;
import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor;

import org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataTableGenerator;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.MetadataSequenceGenerator;

/**
 * An relational accessor.
 * 
 * @author Guy Pelletier
 * @since TopLink EJB 3.0 Reference Implementation
 */
public abstract class NonRelationshipAccessor extends MetadataAccessor {
    /**
     * INTERNAL:
     */
    public NonRelationshipAccessor(MetadataAccessibleObject accessibleObject, MetadataProcessor processor, MetadataDescriptor descriptor) {
        super(accessibleObject, processor, descriptor);
    }
    
    /**
     * INTERNAL:
     */
    public NonRelationshipAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
    }
    
    /**
     * INTERNAL: (Overridden in XMLClassAccessor and XMLBasicAccessor)
	 * Process a @SequenceGenerator into a common metadata sequence generator.
     */
    protected void processSequenceGenerator() {
        SequenceGenerator sequenceGenerator = getAnnotation(SequenceGenerator.class);
        
        if (sequenceGenerator != null) {
            // Ask the common processor to process what we found.
            processSequenceGenerator(new MetadataSequenceGenerator(sequenceGenerator, getJavaClassName()));
        }
    }
    
    /**
     * INTERNAL:
     * Process a MetadataSequenceGenerator and add it to the project.
     */
    protected void processSequenceGenerator(MetadataSequenceGenerator sequenceGenerator) {
        // Check if the sequence generator name uses a reserved name.
        String name = sequenceGenerator.getName();
        
         if (name.equals(MetadataConstants.DEFAULT_TABLE_GENERATOR)) {
            m_validator.throwSequenceGeneratorUsingAReservedName(MetadataConstants.DEFAULT_TABLE_GENERATOR, sequenceGenerator.getLocation());
        } else if (name.equals(MetadataConstants.DEFAULT_IDENTITY_GENERATOR)) {
            m_validator.throwSequenceGeneratorUsingAReservedName(MetadataConstants.DEFAULT_IDENTITY_GENERATOR, sequenceGenerator.getLocation());
        }
            
        // Conflicting means that they do not have all the same values.
        if (m_project.hasConflictingSequenceGenerator(sequenceGenerator)) {
            MetadataSequenceGenerator otherSequenceGenerator = m_project.getSequenceGenerator(name);
            if (sequenceGenerator.loadedFromAnnotations() && otherSequenceGenerator.loadedFromXML()) {
                // WIP - should log a warning that we are ignoring this table generator.
                return;
            } else {
                m_validator.throwConflictingSequenceGeneratorsSpecified(name, sequenceGenerator.getLocation(), otherSequenceGenerator.getLocation());
            }
        }
            
        if (m_project.hasTableGenerator(name)) {
            MetadataTableGenerator otherTableGenerator = m_project.getTableGenerator(name);
            m_validator.throwConflictingSequenceAndTableGeneratorsSpecified(name, sequenceGenerator.getLocation(), otherTableGenerator.getLocation());
        }
            
        for (MetadataTableGenerator otherTableGenerator : m_project.getTableGenerators()) {
            if (otherTableGenerator.getPkColumnValue().equals(sequenceGenerator.getSequenceName())) {
                // generator name will be used instead of an empty sequence name / pk column name
                if(otherTableGenerator.getPkColumnValue().length() > 0) {
                    m_validator.throwConflictingSequenceNameAndTablePkColumnValueSpecified(sequenceGenerator.getSequenceName(), sequenceGenerator.getLocation(), otherTableGenerator.getLocation());
                }
            }
        }
        
        m_project.addSequenceGenerator(sequenceGenerator);
    }
    
    /**
     * INTERNAL: (Overridden in XMLClassAccessor and XMLBasicAccessor)
	 * Process a @TableGenerator into a common metadata table generator.
     */
    protected void processTableGenerator() {
        TableGenerator tableGenerator = getAnnotation(TableGenerator.class);
        
        if (tableGenerator != null) {
            // Ask the common processor to process what we found.
            processTableGenerator(new MetadataTableGenerator(tableGenerator, getJavaClassName()));
        }
    } 
    
    /**
     * INTERNAL:
     * Process a MetadataTableGenerator and add it to the project.
     */     
    protected void processTableGenerator(MetadataTableGenerator tableGenerator) {
        // Check if the table generator name uses a reserved name.
        String name = tableGenerator.getName();
        
        if (name.equals(MetadataConstants.DEFAULT_SEQUENCE_GENERATOR)) {
            m_validator.throwTableGeneratorUsingAReservedName(MetadataConstants.DEFAULT_SEQUENCE_GENERATOR, tableGenerator.getLocation());
        } else if (name.equals(MetadataConstants.DEFAULT_IDENTITY_GENERATOR)) {
            m_validator.throwTableGeneratorUsingAReservedName(MetadataConstants.DEFAULT_IDENTITY_GENERATOR, tableGenerator.getLocation());
        }

        // Conflicting means that they do not have all the same values.
        if (m_project.hasConflictingTableGenerator(tableGenerator)) {
            MetadataTableGenerator otherTableGenerator = m_project.getTableGenerator(name);
            if (tableGenerator.loadedFromAnnotations() && otherTableGenerator.loadedFromXML()) {
                // WIP - should log a warning that we are ignoring this table generator.
                return;
            } else {
                m_validator.throwConflictingTableGeneratorsSpecified(name, tableGenerator.getLocation(), otherTableGenerator.getLocation());
            }
        }
        
        if (m_project.hasSequenceGenerator(tableGenerator.getName())) {
            MetadataSequenceGenerator otherSequenceGenerator = m_project.getSequenceGenerator(name);
            m_validator.throwConflictingSequenceAndTableGeneratorsSpecified(name, otherSequenceGenerator.getLocation(), tableGenerator.getLocation());
        }
            
        for (MetadataSequenceGenerator otherSequenceGenerator : m_project.getSequenceGenerators()) {
            if (otherSequenceGenerator.getSequenceName().equals(tableGenerator.getPkColumnValue())) {
                // generator name will be used instead of an empty sequence name / pk column name
                if(otherSequenceGenerator.getSequenceName().length() > 0) {
                    m_validator.throwConflictingSequenceNameAndTablePkColumnValueSpecified(otherSequenceGenerator.getSequenceName(), otherSequenceGenerator.getLocation(), tableGenerator.getLocation());
                }
            }
        }
            
        // Add the table generator to the descriptor metadata.
        m_project.addTableGenerator(tableGenerator);    
    }
}
