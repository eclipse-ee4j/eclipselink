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
 *     Andrei Ilitchev (Oracle), March 7, 2008 
 *        - New file introduced for bug 211300.  
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;

import org.eclipse.persistence.annotations.ReadTransformer;
import org.eclipse.persistence.annotations.Transformation;
import org.eclipse.persistence.annotations.WriteTransformer;
import org.eclipse.persistence.annotations.WriteTransformers;

import org.eclipse.persistence.internal.jpa.metadata.MetadataLogger;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.transformers.ReadTransformerMetadata;
import org.eclipse.persistence.internal.jpa.metadata.transformers.WriteTransformerMetadata;

import org.eclipse.persistence.mappings.TransformationMapping;

/**
 * TransformationAccessor. Transformation annotation may or may not be present on the
 * accessible object.
 * 
 * @author Andrei Ilitchev
 * @since EclipseLink 1.0 
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
            setFetch((Enum) MetadataHelper.invokeMethod("fetch", transformation));
            setOptional((Boolean) MetadataHelper.invokeMethod("optional", transformation));
        }

        ReadTransformer readTransformer = getAnnotation(ReadTransformer.class);
        if (readTransformer != null) {
            setReadTransformer(readTransformer);
        }

        WriteTransformer writeTransformer = getAnnotation(WriteTransformer.class);
        if (writeTransformer != null) {
            addWriteTransformer(writeTransformer);
        }
        
        WriteTransformers writeTransformers = getAnnotation(WriteTransformers.class);
        if (writeTransformers != null) {
            WriteTransformer[] writeTransformerArray = writeTransformers.value();
            for(int i=0; i<writeTransformerArray.length; i++) {
                addWriteTransformer(writeTransformerArray[i]);
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
    * INTERNAL: (Override from MetadataAccessor)
    */
    public void init(MetadataAccessibleObject accessibleObject, ClassAccessor accessor) {
        super.init(accessibleObject, accessor);
        if (m_readTransformer != null && m_readTransformer.hasClassName()) {
            m_readTransformer.setTransformerClass(getEntityMappings().getClassForName(m_readTransformer.getTransformerClassName()));
        }
        for (WriteTransformerMetadata writeTransformer : m_writeTransformers) {
            writeTransformer.setTransformerClass(getEntityMappings().getClassForName(writeTransformer.getTransformerClassName()));
        }
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
    
    protected void setReadTransformer(ReadTransformer readTransformer) {
        m_readTransformer = new ReadTransformerMetadata();        
        m_readTransformer.setTransformerClass((Class)MetadataHelper.invokeMethod("transformerClass", readTransformer));
        m_readTransformer.setMethod((String)MetadataHelper.invokeMethod("method", readTransformer));
    }

    protected void addWriteTransformer(WriteTransformer writeTransformer) {
        WriteTransformerMetadata writeTransformerMetadata = new WriteTransformerMetadata();        
        writeTransformerMetadata.setTransformerClass((Class)MetadataHelper.invokeMethod("transformerClass", writeTransformer));
        writeTransformerMetadata.setMethod((String)MetadataHelper.invokeMethod("method", writeTransformer));
        writeTransformerMetadata.setColumn(new ColumnMetadata((Column)MetadataHelper.invokeMethod("column", writeTransformer)));
        m_writeTransformers.add(writeTransformerMetadata);
    }
}
