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
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.mappings.transformers.AttributeTransformer;

/**
 * Metadata for ReadTransformer.
 * 
 * @author Andrei Ilitchev
 * @since EclipseLink 1.0 
 */
public class ReadTransformerMetadata {
    private Class m_transformerClass;
    private String m_transformerClassName;
    private String m_method;

    /**
     * INTERNAL:
     */
    public ReadTransformerMetadata() {
        super();
    }

    /**
     * INTERNAL:
     * Class was specified - apply it to the mapping.
     * Note that because this method is called from predeploy with temporary 
     * classloader the class name should be set. However having the class allows 
     * validation.
     */
    protected void applyClass(TransformationMapping mapping) {
        if(AttributeTransformer.class.isAssignableFrom(getTransformerClass())) {
            mapping.setAttributeTransformerClassName(getTransformerClass().getName());
        } else {
            throw ValidationException.readTransformerClassDoesntImplementAttributeTransformer(mapping.getAttributeName(), mapping.getDescriptor().getJavaClassName());
        }
    }
    
    /**
     * INTERNAL:
     * Method was specified - apply it to the mapping.
     */
    protected void applyMethod(TransformationMapping mapping) {
        mapping.setAttributeTransformation(getMethod());
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
    public Class getTransformerClass() {
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
    public boolean hasClass() {
        return m_transformerClass != null && !m_transformerClass.equals(void.class);
    }
    
    /**
     * INTERNAL:
     * Indicates whether there is a class name set. If that's the case
     * then TransformationAccessor sets the corresponding class.
     */
    public boolean hasClassName() {
        return m_transformerClassName != null && m_transformerClassName.length() > 0;
    }
    
    /**
     * INTERNAL:
     */
    public boolean hasMethod() {
        return m_method != null && m_method.length() > 0;
    }
    
    /**
     * INTERNAL:
     * When this method is called there must be either method or class (but not 
     * both!). If there was not class but className, then by now the class 
     * should have been set.
     */
    public void process(TransformationMapping mapping) {
        if(hasMethod()) {
            if(hasClass()) {
                throwBothClassAndMethodSpecifiedException(mapping);
            } else {
                applyMethod(mapping);
            }
        } else {
            if(hasClass()) {
                applyClass(mapping);
            } else {
                throwNeitherClassNorMethodSpecifiedException(mapping);
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
    public void setTransformerClass(Class transformerClass) {
        m_transformerClass = transformerClass;
    }
    
    /**
     * INTERNAL:
     * Used for OX mapping.
     */
    public void setTransformerClassName(String transformerClassName) {
        m_transformerClassName = transformerClassName;
    }
    
    /**
     * INTERNAL:
     */
    protected void throwBothClassAndMethodSpecifiedException(TransformationMapping mapping) {
        throw ValidationException.readTransformerHasBothClassAndMethod(mapping.getAttributeName(), mapping.getDescriptor().getJavaClassName());
    }

    /**
     * INTERNAL:
     */
    protected void throwNeitherClassNorMethodSpecifiedException(TransformationMapping mapping) {
        throw ValidationException.readTransformerHasNeitherClassNorMethod(mapping.getAttributeName(), mapping.getDescriptor().getJavaClassName());
    }
}
