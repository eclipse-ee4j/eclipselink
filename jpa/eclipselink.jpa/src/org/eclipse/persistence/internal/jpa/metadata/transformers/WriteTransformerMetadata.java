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
package org.eclipse.persistence.internal.jpa.metadata.transformers;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;

/**
 * Matadata for WriteTransformer.
 * 
 * @author Andrei Ilitchev
 * @since EclipseLink 1.0 
 */
public class WriteTransformerMetadata extends ReadTransformerMetadata {
    private ColumnMetadata m_column;
    
    /**
     * INTERNAL:
     */
    public WriteTransformerMetadata() {
        super();
    }

    /**
     * INTERNAL:
     */
    protected void applyClass(TransformationMapping mapping) {
        if (FieldTransformer.class.isAssignableFrom(getTransformerClass())) {
            // this is called from predeploy with temp classLoader, therefore should set class name, can't set class.
            mapping.addFieldTransformerClassName(m_column.getDatabaseField(), getTransformerClass().getName());
        } else {
            throw ValidationException.writeTransformerClassDoesntImplementFieldTransformer(mapping.getAttributeName(), mapping.getDescriptor().getJavaClassName(), m_column.getName());
        }
    }
    
    /**
     * INTERNAL:
     */
    protected void applyMethod(TransformationMapping mapping) {
        mapping.addFieldTransformation(m_column.getDatabaseField(), getMethod());
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
     * The method in the parent class calls the overridden methods
     *  applyMethod, applyField, 
     *  throwBothClassAndMethodSpecifiedException, throwNeitherClassNorMethodSpecifiedException
     */
    public void process(TransformationMapping mapping) {
        if (hasFieldName()) {
            super.process(mapping);
        } else {
            throw ValidationException.writeTransformerHasNoColumnName(mapping.getAttributeName(), mapping.getDescriptor().getJavaClassName());
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
    
    /**
     * INTERNAL:
     */
    protected void throwBothClassAndMethodSpecifiedException(TransformationMapping mapping) {
        throw ValidationException.writeTransformerHasBothClassAndMethod(mapping.getAttributeName(), mapping.getDescriptor().getJavaClassName(), m_column.getName());
    }

    /**
     * INTERNAL:
     */
    protected void throwNeitherClassNorMethodSpecifiedException(TransformationMapping mapping) {
        throw ValidationException.writeTransformerHasNeitherClassNorMethod(mapping.getAttributeName(), mapping.getDescriptor().getJavaClassName(), m_column.getName());
    }
}
