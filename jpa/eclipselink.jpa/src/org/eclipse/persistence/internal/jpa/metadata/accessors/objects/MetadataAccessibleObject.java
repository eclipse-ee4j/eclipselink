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
package org.eclipse.persistence.internal.jpa.metadata.accessors.objects;

import java.lang.reflect.Type;
import java.lang.reflect.AnnotatedElement;

import org.eclipse.persistence.internal.jpa.metadata.MetadataHelper;

/**
 * Parent object that is used to hold onto a valid EJB 3.0 decorated method
 * or field.
 * 
 * @author Guy Pelletier
 * @since TopLink 10.1.3/EJB 3.0 Preview
 */
public abstract class MetadataAccessibleObject  {
    private String m_name;
    private Class m_rawClass;
    private Type m_relationType;
    private String m_attributeName;
    private AnnotatedElement m_annotatedElement;
    
    /**
     * INTERNAL:
     */
    public MetadataAccessibleObject(AnnotatedElement annotatedElement) {
        m_annotatedElement = annotatedElement;   
    }
    
    /**
     * INTERNAL:
     * Return the actual field or method.
     */
    public AnnotatedElement getAnnotatedElement() {
        return m_annotatedElement;
    }
    
    /**
     * INTERNAL:
     */
    public String getAttributeName() {
        return m_attributeName;
    }
    
    /**
     * INTERNAL:
     * This should only be called for accessor's of type Map. It will return
     * the Map key type if generics are used, null otherwise.
     */
    public Class getMapKeyClass() {
        if (MetadataHelper.isGenericCollectionType(m_relationType)) {
            // By default, the reference class is equal to the relation
            // class. But if the relation class is a generic we need to 
            // extract and set the actual reference class from the generic. 
            return MetadataHelper.getMapKeyTypeFromGeneric(m_relationType);
        } else {
            return null;
        }
    }
    
    /**
     * INTERNAL:
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * INTERNAL:
     * Return the raw class for this accessible object. E.g. For an 
     * accessible object with a type of java.util.Collection<Employee>, this 
     * method will return java.util.Collection. 
     * @See getReferenceClassFromGeneric() to get Employee.class back.
     */
    public Class getRawClass() {
        if (m_rawClass == null) {
            if (MetadataHelper.isGenericCollectionType(m_relationType)) {
                // By default, the raw class is equal to the relation
                // class. But if the relation class is a generic we need to 
                // extract and set the actual raw class from the generic. 
                m_rawClass = MetadataHelper.getRawClassFromGeneric(m_relationType);
            } else {
                m_rawClass = (Class) m_relationType;
            }
        }
        
        return m_rawClass;    
    }
    
    /**
     * INTERNAL:
     * Return the reference class from the generic specification on this 
     * accessible object.
     * Here is what you will get back from this method given the following
     * scenarios:
     * 1 - public Collection<String> getTasks() => String.class
     * 2 - public Map<String, Integer> getTasks() => Integer.class
     * 3 - public Employee getEmployee() => null
     * 4 - public Collection getTasks() => null
     * 5 - public Map getTasks() => null
     */
    public Class getReferenceClassFromGeneric() {
        if (MetadataHelper.isGenericCollectionType(m_relationType)) {
            return MetadataHelper.getReturnTypeFromGeneric(m_relationType);
        } else {
            return null;
        }
    }
    
    /**
     * INTERNAL:
     * Return the relation type of this accessible object.
     */
    public Type getRelationType() {
        return m_relationType;
    }
    
    /**
     * INTERNAL:
     * Set the annotated element for this accessible object.
     * Once the class loader changes, we need to be able to update our
     * classes.
     */
    public void setAnnotatedElement(AnnotatedElement annotatedElement) {
        m_annotatedElement = annotatedElement;
    }
    
    /**
     * INTERNAL:
     */
    protected void setAttributeName(String attributeName) {
        m_attributeName = attributeName;
    }
    
    /**
     * INTERNAL:
     */
    protected void setName(String name) {
        m_name = name;
    }
    
    /**
     * INTERNAL:
     * Set the relation type of this accessible object.
     */
    protected void setRelationType(Type relationType) {
        m_relationType = relationType;
    }
}
