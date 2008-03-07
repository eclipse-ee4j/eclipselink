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
package org.eclipse.persistence.internal.jpa.metadata.transformers;

import org.eclipse.persistence.annotations.WriteTransformer;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;
import org.eclipse.persistence.mappings.transformers.FieldTransformer;

public class WriteTransformerMetadata extends ReadTransformerMetadata {
    private ColumnMetadata m_column;
    
    public WriteTransformerMetadata() {
        super();
    }

    public WriteTransformerMetadata(WriteTransformer writeTransformer) {
        super();
        setTransformerClass(writeTransformer.transformerClass());
        setMethod(writeTransformer.method());
        setColumn(new ColumnMetadata(writeTransformer.column()));
    }

    public ColumnMetadata getColumn() {
        return m_column;
    }

    public void setColumn(ColumnMetadata column) {
        m_column = column;
    }

    public void process(TransformationMapping mapping) {
        if(hasFieldName()) {
            super.process(mapping);
        } else {
            throw ValidationException.writeTransformerHasNoColumnName(mapping.getAttributeName(), mapping.getDescriptor().getJavaClassName());
        }
    }

    public boolean hasFieldName() {
        return m_column != null && m_column.getName() != null && m_column.getName().length() > 0;   
    }
    
    public void setFieldName(String fieldName) {
        if(m_column == null) {
            m_column = new ColumnMetadata();
        }
        m_column.setName(fieldName);
    }
    
    protected void applyMethod(TransformationMapping mapping) {
        mapping.addFieldTransformation(m_column.getDatabaseField(), getMethod());
    }

    protected void applyClass(TransformationMapping mapping) {
        if(FieldTransformer.class.isAssignableFrom(getTransformerClass())) {
            // this is called from predeploy with temp classLoader, therefore should set class name, can't set class.
            mapping.addFieldTransformerClassName(m_column.getDatabaseField(), getTransformerClass().getName());
        } else {
            throw ValidationException.writeTransformerClassDoesntImplementFieldTransformer(mapping.getAttributeName(), mapping.getDescriptor().getJavaClassName(), m_column.getName());
        }
    }

    protected void applyClassName(TransformationMapping mapping) {
        mapping.addFieldTransformerClassName(m_column.getDatabaseField(), getTransformerClassName());
    }

    protected void throwBothClassAndMethodSpecifiedException(TransformationMapping mapping) {
        throw ValidationException.writeTransformerHasBothClassAndMethod(mapping.getAttributeName(), mapping.getDescriptor().getJavaClassName(), m_column.getName());
    }

    protected void throwNeitherClassNorMethodSpecifiedException(TransformationMapping mapping) {
        throw ValidationException.writeTransformerHasNeitherClassNorMethod(mapping.getAttributeName(), mapping.getDescriptor().getJavaClassName(), m_column.getName());
    }
}
