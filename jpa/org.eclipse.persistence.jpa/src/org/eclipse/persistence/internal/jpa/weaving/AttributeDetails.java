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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.weaving;

import org.eclipse.persistence.internal.libraries.asm.Type;

import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * INTERNAL:
 * Internal helper class that holds details of a persistent attribute.
 * Used by {@link ClassDetails}
 */

public class AttributeDetails {
	
    /** The name of this attribute. */
    protected String attributeName;

    protected String referenceClassName;
    protected Type referenceClassType;

    protected boolean weaveValueHolders = false;
    
    // Determine if we need to weave field level value holders as transient.
    // With JPA 2.0 a mapping may have PROPERTY access when the owning class
    // itself has FIELD access, meaning we want to avoid having the class
    // process a weaved value holder field into a descriptor mapping.
    protected boolean weaveTransientFieldValueHolders;
    
    protected DatabaseMapping mapping;
    
    protected String getterMethodName;
    protected String setterMethodName;
    
    protected boolean attributeOnSuperClass = false;
    
    /** Determines if the attribute has a real field. */
    protected boolean hasField = false;
    
    /** Determines if the attribute has a real field. */
    protected Type declaringType;

    /** Caches the set method signature. */
    protected String setMethodSignature;
    
    protected boolean isVirtualProperty = false;
    
    public AttributeDetails(String attributeName, DatabaseMapping mapping) {
        this.attributeName = attributeName;
        this.mapping = mapping;
    }

    public String getAttributeName() {
        return this.attributeName;
    }
    
    public DatabaseMapping getMapping() {
        return mapping;
    }

    public String getGetterMethodName() {
        return getterMethodName;
    }
    
    public String getSetterMethodSignature() {
        if (setMethodSignature == null) {
            if (isVirtualProperty){
                setMethodSignature = ClassWeaver.VIRTUAL_SETTER_SIGNATURE;
            } else {
                setMethodSignature = "(" + getReferenceClassType().getDescriptor() + ")V";
            }
        }
        return setMethodSignature;
    }
    
    public String getSetterMethodName() {
        return setterMethodName;
    }

    public String getReferenceClassName() {
        return referenceClassName;
    }
    
    public void setReferenceClassName(String className) {
        referenceClassName = className;
    }

    public Type getDeclaringType() {
        return declaringType;
    }
    
    public void setDeclaringType(Type declaringType) {
        this.declaringType = declaringType;
    }

    public Type getReferenceClassType() {
        return referenceClassType;
    }
    
    public void setReferenceClassType(Type classType){
        referenceClassType = classType;
    }
    
    public void setAttributeOnSuperClass(boolean onSuperClass) {
        attributeOnSuperClass = onSuperClass;
    }

    public boolean isVirtualProperty() {
        return isVirtualProperty;
    }

    public void setVirtualProperty(boolean isVirtualProperty) {
        this.isVirtualProperty = isVirtualProperty;
    }

    public boolean isAttributeOnSuperClass() {
        return attributeOnSuperClass;
    }

    public void setWeaveTransientFieldValueHolders() {
        weaveTransientFieldValueHolders = true;
    }
    
    public boolean weaveTransientFieldValueHolders() {
        return weaveTransientFieldValueHolders;
    }
    
    public boolean weaveValueHolders() {
        return weaveValueHolders;
    }
    
    public void weaveVH(boolean weaveValueHolders, DatabaseMapping mapping) {
        this.weaveValueHolders = weaveValueHolders;
    }
    
    public void setGetterMethodName(String getMethodName){
        this.getterMethodName = getMethodName;
    }
    
    public void setSetterMethodName(String setMethodName){
        this.setterMethodName = setMethodName;
    }
    
    /**
     * Set if the attribute has a real field.
     * This allows properties to still be weaved at the field level.
     */
    public void setHasField(boolean hasField) {
        this.hasField = hasField;
    }
    
    /**
     * Return if the attribute has a real field.
     * This allows properties to still be weaved at the field level.
     */
    public boolean hasField() {
        return hasField;
    }
    
    public boolean isCollectionMapping() {
        return mapping.isCollectionMapping();
    }

    public boolean isMappedWithAttributeAccess(){
        return getterMethodName == null;
    }
    
    public boolean isOneToOneMapping() {
        return mapping.isOneToOneMapping();
    }
	
    public boolean isLazy() {
        return mapping.isLazy();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(attributeName);
        if (referenceClassName != null) {
            sb.append("[");
            sb.append(referenceClassName);
            sb.append("]");
        }
        sb.append(" weaveVH: ");
        if (weaveValueHolders()) {
            sb.append("true");
        }
        else {
            sb.append("false");
        }
        sb.append(" CM: ");
        if (isCollectionMapping()) {
            sb.append("true");
        }
        else {
            sb.append("false");
        }
        return sb.toString();
    }

}
