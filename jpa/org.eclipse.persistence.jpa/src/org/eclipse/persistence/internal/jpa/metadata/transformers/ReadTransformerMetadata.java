/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
 *       - 218084: Implement metadata merging functionality between mapping file   
 *     04/27/2010-2.1 Guy Pelletier 
 *       - 309856: MappedSuperclasses from XML are not being initialized properly
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.metadata.transformers;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.ORMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataClass;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;

/**
 * INTERNAL:
 * Metadata for ReadTransformer.
 * 
 * Key notes:
 * - any metadata mapped from XML to this class must be compared in the
 *   equals method.
 * - when loading from annotations, the constructor accepts the metadata
 *   accessor this metadata was loaded from. Used it to look up any 
 *   'companion' annotation needed for processing.
 * - methods should be preserved in alphabetical order.
 * 
 * @author Andrei Ilitchev
 * @since EclipseLink 1.0 
 */
public class ReadTransformerMetadata extends ORMetadata {
    private MetadataClass m_transformerClass;
    
    private String m_transformerClassName;
    private String m_method;

    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public ReadTransformerMetadata() {
        super("<read-transformer>");
    }
    
    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public ReadTransformerMetadata(MetadataAnnotation readTransformer, MetadataAccessor accessor) {
        super(readTransformer, accessor);
    
        if (readTransformer != null) {
            m_transformerClass = getMetadataClass((String) readTransformer.getAttributeString("transformerClass"));
            m_method = (String) readTransformer.getAttributeString("method");
        }
    }
    
    /**
     * INTERNAL:
     * Used for XML loading from subclasses.
     */
    protected ReadTransformerMetadata(String xmlElement) {
        super(xmlElement);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (objectToCompare instanceof ReadTransformerMetadata) {
            ReadTransformerMetadata readTransformer = (ReadTransformerMetadata) objectToCompare;
            
            if (! valuesMatch(m_transformerClassName, readTransformer.getTransformerClassName())) {
                return false;
            }
            
            return valuesMatch(m_method, readTransformer.getMethod());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getMethod() {
        return m_method;
    }
    
    /**
     * INTERNAL:
     */
    public MetadataClass getTransformerClass() {
        return m_transformerClass;
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public String getTransformerClassName() {
        return m_transformerClassName;
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);
    
        m_transformerClass = initXMLClassName(m_transformerClassName);
    }
    
    /**
     * INTERNAL:
     * When this method is called there must be either method or class (but not 
     * both!). If there was not class but className, then by now the class 
     * should have been set.
     */
    public void process(TransformationMapping mapping, String annotatedElementName) {
        if (m_method == null || m_method.equals("")) {
            if (m_transformerClass.isVoid()) {
                throw ValidationException.readTransformerHasNeitherClassNorMethod(annotatedElementName);
            } else {
                if (m_transformerClass.extendsInterface(AttributeTransformer.class)) {
                    mapping.setAttributeTransformerClassName(m_transformerClass.getName());
                } else {
                    throw ValidationException.readTransformerClassDoesntImplementAttributeTransformer(annotatedElementName);
                }
            }
        } else {
            if (m_transformerClass.isVoid()) {
                mapping.setAttributeTransformation(m_method);
            } else {
                throw ValidationException.readTransformerHasBothClassAndMethod(annotatedElementName);
            }
        }
    }

    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setMethod(String method) {
        m_method = method;
    }
    
    /**
     * INTERNAL:
     */
    public void setTransformerClass(MetadataClass transformerClass) {
        m_transformerClass = transformerClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTransformerClassName(String transformerClassName) {
        m_transformerClassName = transformerClassName;
    }
}
