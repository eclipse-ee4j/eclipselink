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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.annotations.Mutable;
import org.eclipse.persistence.annotations.ReadTransformer;
import org.eclipse.persistence.annotations.Transformation;
import org.eclipse.persistence.annotations.WriteTransformer;
import org.eclipse.persistence.annotations.WriteTransformers;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.transformers.ReadTransformerMetadata;
import org.eclipse.persistence.internal.jpa.metadata.transformers.WriteTransformerMetadata;

import org.eclipse.persistence.mappings.TransformationMapping;

/**
 * TransformationAccessor. Transformation annotation may or may not be present on the
 * accessible object.
 * 
 * @author Andrei Ilitchev
 * @since TopLink EJB 3.0 Reference Implementation
 */
public class TransformationAccessor extends BasicAccessor {
    private ReadTransformerMetadata m_readTransformer;
    private List<WriteTransformerMetadata> m_writeTransformers = new ArrayList<WriteTransformerMetadata>();
    
    /**
     * INTERNAL:
     */
    public TransformationAccessor() {
        super();
    }
    
    /**
     * INTERNAL:
     */
    public TransformationAccessor(MetadataAccessibleObject accessibleObject, ClassAccessor classAccessor) {
        super(accessibleObject, classAccessor);
        
        Transformation transformation = getAnnotation(Transformation.class);
        if (transformation != null) {
            setFetch(transformation.fetch());
            setOptional(transformation.optional());
        }

        Mutable mutable = getAnnotation(Mutable.class);
        if (mutable != null) {
            setMutable(mutable.value());
        }

        ReadTransformer readTransformer = getAnnotation(ReadTransformer.class);
        if (readTransformer != null) {
            m_readTransformer = new ReadTransformerMetadata(readTransformer);
        }

        WriteTransformer writeTransformer = getAnnotation(WriteTransformer.class);
        if (writeTransformer != null) {
            m_writeTransformers.add(new WriteTransformerMetadata(writeTransformer));
        }
        
        WriteTransformers writeTransformers = getAnnotation(WriteTransformers.class);
        if (writeTransformers != null) {
            WriteTransformer[] writeTransformerArray = writeTransformers.value();
            for(int i=0; i<writeTransformerArray.length; i++) {
                m_writeTransformers.add(new WriteTransformerMetadata(writeTransformerArray[i]));
            }
        }

        //TODO: ReturningPolicy
    }
    
    public ReadTransformerMetadata getReadTransformer() {
        return m_readTransformer;
    }
    
    public List<WriteTransformerMetadata> getWriteTransformers() {
        return m_writeTransformers;
    }
    
    /**
     * INTERNAL:
     * Return true if this accessor represents transformation mapping.
     */
    public boolean isTransformation() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Process a basic accessor.
     */
    public void process() {
        if (getDescriptor().hasMappingForAttributeName(getAttributeName())) {
            // Ignore the mapping if one already exists for it.
            getLogger().logWarningMessage(MetadataLogger.IGNORE_MAPPING, this);
        } else {
            processTransformationMapping();
        }
    }

    /**
     * INTERNAL:
     * Creates TransformationMapping and adds it to descriptor. 
     */
    protected void processTransformationMapping() {
        TransformationMapping mapping = new TransformationMapping();
        mapping.setAttributeName(getAttributeName());
        mapping.setIsOptional(isOptional());
        mapping.setIsLazy(usesIndirection());
        if(getMutable() != null) {
            mapping.setIsMutable(getMutable().booleanValue());
        }

        // Will check for PROPERTY access.
        setAccessorMethods(mapping);

        // Add the mapping to the descriptor early so that
        // in case of exception the mapped class could be included into exception message.
        getDescriptor().addMapping(mapping);

        if(m_readTransformer != null) {
            m_readTransformer.process(mapping);
        }
        
        if(m_writeTransformers.isEmpty()) {
            mapping.setIsReadOnly(true);
        } else {
            if(m_writeTransformers.size() == 1) {
                // If only one WriteTransformer specified then if column name is not specified
                // attribute name will be used as a column name.
                if(!m_writeTransformers.get(0).hasFieldName()) {
                    m_writeTransformers.get(0).setFieldName(getAttributeName());
                }
            }
            for(Iterator<WriteTransformerMetadata> it = m_writeTransformers.iterator(); it.hasNext(); ) {
                it.next().process(mapping);
            }
        }

        //TODO: ReturningPolicy
    }

    public void setReadTransformer(ReadTransformerMetadata readTransformer) {
        m_readTransformer = readTransformer;
    }
    
    public void setWriteTransformers(List<WriteTransformerMetadata> writeTransformers) {
        m_writeTransformers = writeTransformers;
    }
}
