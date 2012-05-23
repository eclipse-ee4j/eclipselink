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
 *     Andrei Ilitchev (Oracle), March 7, 2008 
 *        - New file introduced for bug 211300.
 *     05/16/2008-1.0M8 Guy Pelletier 
 *       - 218084: Implement metadata merging functionality between mapping files 
 *     02/06/2009-2.0 Guy Pelletier 
 *       - 248293: JPA 2.0 Element Collections (part 2)
 *     03/27/2009-2.0 Guy Pelletier 
 *       - 241413: JPA 2.0 Add EclipseLink support for Map type attributes
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.accessors.mappings;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.annotations.ReadTransformer;
import org.eclipse.persistence.annotations.WriteTransformer;
import org.eclipse.persistence.annotations.WriteTransformers;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotatedElement;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;

import org.eclipse.persistence.internal.jpa.metadata.transformers.ReadTransformerMetadata;
import org.eclipse.persistence.internal.jpa.metadata.transformers.WriteTransformerMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import org.eclipse.persistence.mappings.TransformationMapping;

/**
 * INTERNAL:
 * TransformationAccessor. Transformation annotation may or may not be present 
 * on the accessible object.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - any metadata mapped from XML to this class must be handled in the merge
 *   method. (merging is done at the accessor/mapping level)
 * - any metadata mapped from XML to this class must be initialized in the
 *   initXMLObject  method.
 * - methods should be preserved in alphabetical order.
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
        super("<transformation>");
    }
    
    /**
     * INTERNAL:
     */
    public TransformationAccessor(MetadataAnnotation transformation, MetadataAnnotatedElement annotatedElement, ClassAccessor classAccessor) {
        super(transformation, annotatedElement, classAccessor);
        
        if (transformation != null) {
            setFetch((String) transformation.getAttribute("fetch"));
            setOptional((Boolean) transformation.getAttribute("optional"));
        }
        
        if (isAnnotationPresent(ReadTransformer.class)) {
            m_readTransformer = new ReadTransformerMetadata(getAnnotation(ReadTransformer.class), this);
        }
        
        // Set the write transformers if specified. 
        // Process all the write transformers first.
        if (isAnnotationPresent(WriteTransformers.class)) {
            for (Object transformer : (Object[]) getAnnotation(WriteTransformers.class).getAttributeArray("value")) {
                m_writeTransformers.add(new WriteTransformerMetadata((MetadataAnnotation) transformer, this));
            }
        }
        
        // Process the single write transformer second.
        if (isAnnotationPresent(WriteTransformer.class)) {
            m_writeTransformers.add(new WriteTransformerMetadata(getAnnotation(WriteTransformer.class), this));
        }
        
        // TODO: ReturningPolicy
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof TransformationAccessor) {
            TransformationAccessor transformationAccessor = (TransformationAccessor) objectToCompare;
            
            if (! valuesMatch(m_readTransformer, transformationAccessor.getReadTransformer())) {
                return false;
            }
            
            return valuesMatch(m_writeTransformers, transformationAccessor.getWriteTransformers());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ReadTransformerMetadata getReadTransformer() {
        return m_readTransformer;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public List<WriteTransformerMetadata> getWriteTransformers() {
        return m_writeTransformers;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
        
        // Init the ORMetadata objects.
        initXMLObject(m_readTransformer, accessibleObject);
        
        // Init the list of ORMetadata objects.
        initXMLObjects(m_writeTransformers, accessibleObject);
    }
    
    /**
     * INTERNAL:
     * Process a transformation accessor. Creates a TransformationMapping and 
     * adds it to descriptor.
     */
    @Override
    public void process() {
        TransformationMapping mapping = new TransformationMapping();
        setMapping(mapping);
        
        mapping.setAttributeName(getAttributeName());
        mapping.setIsOptional(isOptional());
        mapping.setIsLazy(usesIndirection());
        if (getMutable() != null) {
            mapping.setIsMutable(getMutable().booleanValue());
        }

        // Will check for PROPERTY access.
        setAccessorMethods(mapping);

        if (m_readTransformer != null) {
            m_readTransformer.process(mapping, getAnnotatedElementName());
        }
        
        if (m_writeTransformers.isEmpty()) {
            mapping.setIsReadOnly(true);
        } else {
            if (m_writeTransformers.size() == 1) {
                // If only one WriteTransformer specified then if column name 
                // is not specified attribute name will be used as a column 
                // name.
                if (! m_writeTransformers.get(0).hasFieldName()) {
                    m_writeTransformers.get(0).setFieldName(getAttributeName());
                }
            }
            
            for (WriteTransformerMetadata writeTransformer : m_writeTransformers) {
                writeTransformer.process(mapping, getAnnotatedElementName());
            }
        }

        // TODO: ReturningPolicy
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setReadTransformer(ReadTransformerMetadata readTransformer) {
        m_readTransformer = readTransformer;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setWriteTransformers(List<WriteTransformerMetadata> writeTransformers) {
        m_writeTransformers = writeTransformers;
    }
}
