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

import org.eclipse.persistence.annotations.ReadTransformer;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;

public class ReadTransformerMetadata {
    private Class m_transformerClass;
    private String m_transformerClassName;
    private String m_method;

    public ReadTransformerMetadata() {
    }

    public ReadTransformerMetadata(ReadTransformer readTransformer) {
        setTransformerClass(readTransformer.transformerClass());
        setMethod(readTransformer.method());
    }

    public Class getTransformerClass() {
        return m_transformerClass;
    }

    public void setTransformerClass(Class transformerClass) {
        m_transformerClass = transformerClass;
    }
    
    public String getTransformerClassName() {
        return m_transformerClassName;
    }

    public void setTransformerClassName(String transformerClassName) {
        m_transformerClassName = transformerClassName;
    }
    
    public String getMethod() {
        return m_method;
    }

    public void setMethod(String method) {
        m_method = method;
    }
    
    public boolean hasMethod() {
        return m_method != null && m_method.length() > 0;
    }
    
    public boolean hasClass() {
        return m_transformerClass != null && !m_transformerClass.equals(void.class);
    }
    
    public boolean hasClassName() {
        return m_transformerClassName != null && m_transformerClassName.length() > 0;
    }
    
    public void process(TransformationMapping mapping) {
        if(hasMethod()) {
            if(!hasClass() && !hasClassName()) {
                applyMethod(mapping);
            } else {
                throwBothClassAndMethodSpecifiedException(mapping);
            }
        } else {
            if(hasClass()) {
                applyClass(mapping);
            } else if(hasClassName()) {
                applyClassName(mapping);
            } else {
                throwNeitherClassNorMethodSpecifiedException(mapping);
            }
        }
    }

    protected void applyMethod(TransformationMapping mapping) {
        mapping.setAttributeTransformation(getMethod());
    }

    protected void applyClass(TransformationMapping mapping) {
        if(AttributeTransformer.class.isAssignableFrom(getTransformerClass())) {
            // this is called from predeploy with temp classLoader, therefore should set class name, can't set class.
            mapping.setAttributeTransformerClassName(getTransformerClass().getName());
        } else {
            throw ValidationException.readTransformerClassDoesntImplementAttributeTransformer(mapping.getAttributeName(), mapping.getDescriptor().getJavaClassName());
        }
    }

    protected void applyClassName(TransformationMapping mapping) {
        mapping.setAttributeTransformerClassName(getTransformerClassName());
    }

    protected void throwBothClassAndMethodSpecifiedException(TransformationMapping mapping) {
        throw ValidationException.readTransformerHasBothClassAndMethod(mapping.getAttributeName(), mapping.getDescriptor().getJavaClassName());
    }

    protected void throwNeitherClassNorMethodSpecifiedException(TransformationMapping mapping) {
        throw ValidationException.readTransformerHasNeitherClassNorMethod(mapping.getAttributeName(), mapping.getDescriptor().getJavaClassName());
    }
}
