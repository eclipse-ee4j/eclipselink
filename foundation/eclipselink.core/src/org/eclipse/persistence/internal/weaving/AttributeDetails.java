/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.weaving;

import org.eclipse.persistence.internal.libraries.asm.Type;

import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * INTERNAL:
 * Internal helper class that holds details of a persistent attribute.
 * Used by {@link ClassDetails}
 */

public class AttributeDetails {
	
    // the name of this attribute (obviously!)
    protected String attributeName = null;

    protected String referenceClassName = null;
    protected Type referenceClassType = null;

    protected boolean weaveValueHolders = false;
    
    protected DatabaseMapping mapping = null;
    
    protected String getterMethodName = null;
    protected String setterMethodName = null;
    
    protected boolean attributeOnSuperClass = false;

    public AttributeDetails(String attributeName, DatabaseMapping mapping) {
        this.attributeName = attributeName;
        this.mapping = mapping;
    }

    public String getAttributeName() {
        return this.attributeName;
    }
    
    public DatabaseMapping getMapping(){
        return mapping;
    }

    public String getGetterMethodName(){
        return getterMethodName;
    }
    
    public String getSetterMethodName(){
        return setterMethodName;
    }

    public String getReferenceClassName() {
        return referenceClassName;
    }
    
    public void setReferenceClassName(String className){
        referenceClassName = className;
    }

    public Type getReferenceClassType() {
        return referenceClassType;
    }
    
    public void setReferenceClassType(Type classType){
        referenceClassType = classType;
    }
    
    public void setAttributeOnSuperClass(boolean onSuperClass){
        attributeOnSuperClass = onSuperClass;
    }

    public boolean isAttributeOnSuperClass(){
        return attributeOnSuperClass;
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
    
    public boolean isCollectionMapping() {
        return mapping.isCollectionMapping();
    }

    public boolean isMappedWithAttributeAccess(){
        return getterMethodName == null;
    }
    
    public boolean isOneToOneMapping() {
        return mapping.isOneToOneMapping();
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
