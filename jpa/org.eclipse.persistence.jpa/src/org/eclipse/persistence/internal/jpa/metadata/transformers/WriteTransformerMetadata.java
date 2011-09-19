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
import org.eclipse.persistence.internal.jpa.metadata.accessors.MetadataAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAccessibleObject;
import org.eclipse.persistence.internal.jpa.metadata.accessors.objects.MetadataAnnotation;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.PrimaryKeyJoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;

/**
 * INTERNAL:
 * Matadata for WriteTransformer.
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
public class WriteTransformerMetadata extends ReadTransformerMetadata {
    private ColumnMetadata m_column;
    
    /**
     * INTERNAL:
     * Used for XML loading.
     */
    public WriteTransformerMetadata() {
        super("<write-transformer>");
    }

    /**
     * INTERNAL:
     * Used for annotation loading.
     */
    public WriteTransformerMetadata(MetadataAnnotation writeTransformer, MetadataAccessor accessor) {
        super(writeTransformer, accessor);
        
        m_column = new ColumnMetadata((MetadataAnnotation) writeTransformer.getAttribute("column"), accessor);
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public boolean equals(Object objectToCompare) {
        if (super.equals(objectToCompare) && objectToCompare instanceof WriteTransformerMetadata) {
            WriteTransformerMetadata writeTransformer = (WriteTransformerMetadata) objectToCompare;
            return valuesMatch(m_column, writeTransformer.getColumn());
        }
        
        return false;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public ColumnMetadata getColumn() {
        return m_column;
    }

    /**
     * INTERNAL:
     * Indicates whether there is a column name.
     */
    public boolean hasFieldName() {
        return m_column != null && m_column.getName() != null && m_column.getName().length() > 0;   
    }
    
    /**
     * INTERNAL:
     */
    @Override
    public void initXMLObject(MetadataAccessibleObject accessibleObject, XMLEntityMappings entityMappings) {
        super.initXMLObject(accessibleObject, entityMappings);

        initXMLObject(m_column, accessibleObject);
    }

    /**
     * INTERNAL:
     */
    public void process(TransformationMapping mapping, String annotatedElementName) {
        if (hasFieldName()) {
            if (getMethod() == null || getMethod().equals("")) {
                if (getTransformerClass().isVoid()) {
                    throw ValidationException.writeTransformerHasNeitherClassNorMethod(annotatedElementName, m_column.getName());
                } else {
                    if (getTransformerClass().extendsInterface(FieldTransformer.class)) {
                        mapping.addFieldTransformerClassName(m_column.getDatabaseField(), getTransformerClass().getName());
                    } else {
                        throw ValidationException.writeTransformerClassDoesntImplementFieldTransformer(annotatedElementName, m_column.getName());
                    }
                }
            } else {
                if (getTransformerClass().isVoid()) {
                    mapping.addFieldTransformation(m_column.getDatabaseField(), getMethod());
                } else {
                    throw ValidationException.writeTransformerHasBothClassAndMethod(annotatedElementName, m_column.getName());
                }
            }
        } else {
            throw ValidationException.writeTransformerHasNoColumnName(annotatedElementName);
        }
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setColumn(ColumnMetadata column) {
        m_column = column;
    }
    
    /**
     * INTERNAL:
     * The name may be set by TransformationAccessor in case there's none 
     * specified.
     */
    public void setFieldName(String fieldName) {
        if (m_column == null) {
            m_column = new ColumnMetadata();
        }
        
        m_column.setName(fieldName);
    }
}
