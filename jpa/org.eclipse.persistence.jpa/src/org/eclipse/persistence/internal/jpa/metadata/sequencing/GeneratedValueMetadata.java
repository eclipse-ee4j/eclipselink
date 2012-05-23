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
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     07/23/2010-2.2 Guy Pelletier 
 *       - 237902: DDL GEN doesn't qualify SEQUENCE table with persistence unit schema
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.sequencing;

import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.MetadataDescriptor;
import org.eclipse.persistence.internal.jpa.metadata.MetadataProject;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.DatasourceLogin;

import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_GENERATION_AUTO;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_GENERATION_IDENTITY;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_GENERATION_SEQUENCE;
import static org.eclipse.persistence.internal.jpa.metadata.MetadataConstants.JPA_GENERATION_TABLE;

/**
 * Metadata object to hold generated value information.
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
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class GeneratedValueMetadata extends ORMetadata {
    private String m_strategy;
    private String m_generator;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public GeneratedValueMetadata() {}
    
    /**
     * INTERNAL:
     * Used annotation loading.
     */
    public GeneratedValueMetadata(MetadataAnnotation generatedValue, MetadataAccessor accessor) {
        super(generatedValue, accessor);
        
        m_generator = (String) generatedValue.getAttributeString("generator");
        m_strategy = (String) generatedValue.getAttribute("strategy"); 
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof GeneratedValueMetadata) {
            GeneratedValueMetadata generatedValue = (GeneratedValueMetadata) objectToCompare;
            
            if (! valuesMatch(m_generator, generatedValue.getGenerator())) {
                return false;
            }
            
            return valuesMatch(m_strategy, generatedValue.getStrategy());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getGenerator() {
        return m_generator;
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
     */
    public void process(MetadataDescriptor descriptor, Map<String, Sequence> sequences, DatasourceLogin login) {
        // If the generator value is null then it was loaded from XML (and it 
        // wasn't specified) so assign it the annotation default of ""
        if (m_generator == null) {
            m_generator = "";
        }

        Sequence sequence = sequences.get(m_generator);
            
        if (sequence == null) {
            // A null strategy will default to AUTO.
            if (m_strategy == null || m_strategy.equals(JPA_GENERATION_AUTO)) {
                if (sequences.containsKey(MetadataProject.DEFAULT_AUTO_GENERATOR)) {
                    login.setDefaultSequence(sequences.get(MetadataProject.DEFAULT_AUTO_GENERATOR));
                }
            } else if (m_strategy.equals(JPA_GENERATION_TABLE)) {
                if (m_generator.equals("")) {
                    sequence = sequences.get(MetadataProject.DEFAULT_TABLE_GENERATOR);
                } else {
                    sequence = (Sequence) sequences.get(MetadataProject.DEFAULT_TABLE_GENERATOR).clone();
                    sequence.setName(m_generator);
                }
            } else if (m_strategy.equals(JPA_GENERATION_SEQUENCE)) {
                if (m_generator.equals("")) {
                    sequence = sequences.get(MetadataProject.DEFAULT_SEQUENCE_GENERATOR);
                } else {
                    sequence = (Sequence) sequences.get(MetadataProject.DEFAULT_SEQUENCE_GENERATOR).clone();
                    sequence.setName(m_generator);
                }
            } else if (m_strategy.equals(JPA_GENERATION_IDENTITY)) {
                if (m_generator.equals("")) {
                    sequence = sequences.get(MetadataProject.DEFAULT_IDENTITY_GENERATOR);
                } else {
                    sequence = (Sequence) sequences.get(MetadataProject.DEFAULT_IDENTITY_GENERATOR).clone();
                    sequence.setName(m_generator);
                }
            }
       }

       if (sequence != null) {
           descriptor.setSequenceNumberName(sequence.getName());
           login.addSequence(sequence);
       } else {
           String seqName;

           if (m_generator.equals("")) {
               if (sequences.containsKey(MetadataProject.DEFAULT_AUTO_GENERATOR)) {
                   seqName = sequences.get(MetadataProject.DEFAULT_AUTO_GENERATOR).getName();
               } else {
                   seqName = MetadataProject.DEFAULT_AUTO_GENERATOR;
               }
           } else {
               seqName = m_generator;
           }

           descriptor.setSequenceNumberName(seqName);
       }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setGenerator(String generator) {
        m_generator = generator;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setStrategy(String strategy) {
        m_strategy = strategy;
    }
}
