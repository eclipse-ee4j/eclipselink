/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jaxb.compiler;

import java.util.Iterator;
import java.util.Collection;
import javax.xml.namespace.QName;
import org.eclipse.persistence.jaxb.javamodel.Helper;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.JavaHasAnnotations;
import org.eclipse.persistence.jaxb.javamodel.JavaMethod;

/**
 *  INTERNAL:
 *  <p><b>Purpose:</b>To store information about a property on a class during JAXB 2.0 Generation
 *  <p><b>Responsibilities:</b><ul>
 *  <li>Store information about the java property such as property name, if it's a method or field,
 *  and it's type.</li>
 *  <li>Store some schema-specific information such as the schema name, the schema type, and mimeType</li>
 *  <li>Store some JAXB 2.0 Runtime specific information such as JAXB 2.0 Adapter classes</li>
 *  </ul>
 *  <p>This class is used to store information about a property on a JAXB 2.0 annotated class. It is 
 *  created by the AnnotationsProcessor duing pre-processing and stored on a TypeInfo object
 *  
 *  @see org.eclipse.persistence.jaxb.compiler.TypeInfo
 *  @see org.eclipse.persistence.jaxb.compiler.AnnotationsProcessor
 *  
 *  @since   release specific (what release of product did this appear in)
 *  @author mmacivor
 */
public class TypeProperty {
    private String propertyName;
    private QName schemaName;
    private boolean isMethodProperty;
    private QName schemaType;
    private boolean isSwaAttachmentRef;
    private boolean isMtomAttachment;
    private String mimeType;
    private JavaClass type;
    private JavaClass adapterClass;
    private JavaClass valueType;
    private JavaHasAnnotations element;
    private JavaClass genericType;
    private boolean isAttribute = false;
    private boolean isChoice = false;
    private Helper helper;
    private String getMethodName;
    private String setMethodName;
    private boolean isRequired = false;
    private Collection<TypeProperty> choiceProperties;
    
    public TypeProperty() {
    }

    public TypeProperty(Helper helper) {
        this.helper = helper;
    }

    public void setHelper(Helper helper) {
        this.helper = helper;
    }
    
    public JavaClass getAdapterClass() {
        return adapterClass;
    }

    public boolean hasAdapterClass() {
        return getAdapterClass() != null;
    }
    
    public void setAdapterClass(Class adapterCls) {
        setAdapterClass(helper.getJavaClass(adapterCls));
    }
    
    public void setAdapterClass(JavaClass adapterCls) {
        adapterClass = adapterCls;
        valueType = helper.getJavaClass(Object.class);

        // look for marshal method
        for (Iterator<JavaMethod> methodIt = adapterClass.getMethods().iterator(); methodIt.hasNext(); ) {
            JavaMethod method = methodIt.next();
            // for some reason, getDeclaredMethods is returning inherited
            // methods - need to filter
            //if (method.getName().equals("marshal") && method.getReturnType() != Object.class && method.getParameterTypes()[0] != Object.class) {
            // TODO verify that inherited marshal methods are not being returned...
            if (method.getName().equals("marshal")) {
                valueType = (JavaClass) method.getReturnType();
                break;
            }
        }
    }

    public JavaClass getValueType() {
        return valueType;
    }
    
    public JavaHasAnnotations getElement() {
        return element;
    }
    
    public void setElement(JavaHasAnnotations element) {
        this.element = element;
    }

    public String getPropertyName() {
        return propertyName;
    }
    
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public QName getSchemaName() {
        return this.schemaName;
    }
    
    public void setSchemaName(QName schemaName) {
        this.schemaName = schemaName;
    }
    
    public boolean isMethodProperty() {
        return isMethodProperty;
    }
    
    public void setMethodProperty(boolean isMethod) {
        isMethodProperty = isMethod;
    }
    
    public void setType(JavaClass cls) {
        type = cls;
    }
    
    public JavaClass getType() {
        return type;
    }
    
    public JavaClass getGenericType() {
        return genericType;
    }
    
    public void setGenericType(JavaClass genericType) {
        this.genericType = genericType;
    }
    
    public QName getSchemaType() {
        return schemaType;
    }
    
    public void setSchemaType(QName type) {
        schemaType = type;
    }
    
    public boolean isSwaAttachmentRef() {
        return isSwaAttachmentRef;
    }
    
    public void setIsSwaAttachmentRef(boolean b) {
        isSwaAttachmentRef = b;
    }
    
    public boolean isMtomAttachment() {
        return isMtomAttachment;
    }
    
    public void setIsMtomAttachment(boolean b) {
        isMtomAttachment = b;
    }
    
    public boolean isRequired() {
        return isRequired;
    }
    
    public void setIsRequired(boolean b) {
        isRequired = b;
    }

    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mType) {
        mimeType = mType;
    }
    
    public boolean isAttribute() {
        return isAttribute;
    }
    
    public void setIsAttribute(boolean attribute) {
        isAttribute = attribute;
    }
    
    public String getGetMethodName() {
        return getMethodName;
    }
    
    public void setGetMethodName(String methodName) {
        getMethodName = methodName;
    }
    
    public String getSetMethodName() {
        return setMethodName;
    }
    
    public void setSetMethodName(String methodName) {
        setMethodName = methodName;
    }
    
    public void setChoice(boolean b) {
        isChoice = b;
    }
    
    public boolean isChoice() {
        return isChoice;
    }
    
    public Collection<TypeProperty> getChoiceProperties() {
        return this.choiceProperties;
    }
    
    public void setChoiceProperties(Collection<TypeProperty> properties) {
        this.choiceProperties = properties;
    }
}
