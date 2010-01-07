/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* mmacivor - June 05/2008 - 1.0 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.jaxb.compiler;

import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.TypeMappingInfo;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;

import java.util.List;
import java.util.ArrayList;

public class ElementDeclaration {
    private QName elementName;
    private QName substitutionHead;
    private String javaTypeName;
    private JavaClass javaType;    
    private JavaClass adaptedJavaType;
    private String adaptedJavaTypeName;    
    private List<ElementDeclaration> substitutableElements;
    private boolean isXmlRootElement = false;
    private boolean isList = false;
    private Class javaTypeAdapterClass;
    private Class scopeClass;
    private String defaultValue;
    private TypeMappingInfo typeMappingInfo;
    private boolean xmlAttachmentRef;
    private String xmlMimeType;
    
    public ElementDeclaration(QName name, JavaClass javaType, String javaTypeName, boolean isList) {
        this.elementName = name;
        this.javaTypeName = javaTypeName;
        this.javaType = javaType;
        this.substitutableElements = new ArrayList<ElementDeclaration>();
        this.isList = isList;
        this.scopeClass = XmlElementDecl.GLOBAL.class;
    }
    
    public ElementDeclaration(QName name, JavaClass javaType, String javaTypeName, boolean isList, Class scopeClass) {
    	this(name, javaType, javaTypeName, isList);
    	this.scopeClass = scopeClass;        
    }
    
    public QName getElementName() {
        return elementName;
    }
    
    public String getJavaTypeName() {
        return javaTypeName;
    }
    
    public List<ElementDeclaration> getSubstitutableElements() {
        return substitutableElements;
    }
    
    public void addSubstitutableElement(ElementDeclaration element) {
        this.substitutableElements.add(element);
    }
    
    public void setSubstitutionHead(QName rootElement) {
        this.substitutionHead = rootElement;
    }
    
    public QName getSubstitutionHead() {
        return substitutionHead;
    }
    
    public boolean isXmlRootElement() {
        return this.isXmlRootElement;
    }
    
    public void setIsXmlRootElement(boolean isXmlRoot) {
        this.isXmlRootElement = isXmlRoot;
    }
    
    public JavaClass getJavaType() {
        return this.javaType;
    }
    
    public void setJavaType(JavaClass type) {
        this.javaType = type;
        this.javaTypeName = type.getQualifiedName();
    }

    public boolean isList() {
        return isList;
    }

    public void setList(boolean isList) {
        this.isList = isList;
    }

    public Class getJavaTypeAdapterClass() {
        return javaTypeAdapterClass;
    }

    public void setJavaTypeAdapterClass(Class javaTypeAdapterClass) {
        this.javaTypeAdapterClass = javaTypeAdapterClass;
    }

    public JavaClass getAdaptedJavaType() {
        return adaptedJavaType;
    }

    public void setAdaptedJavaType(JavaClass adaptedJavaType) {
        this.adaptedJavaType = adaptedJavaType;
        this.adaptedJavaTypeName = adaptedJavaType.getQualifiedName();
    }

    public String getAdaptedJavaTypeName() {
        return adaptedJavaTypeName;
    }

    public Class getScopeClass() {
        return scopeClass;
    }
    
    public void setScopeClass(Class scopeClass) {
        this.scopeClass = scopeClass;
    }
    
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }
    
    public TypeMappingInfo getTypeMappingInfo() {
        return this.typeMappingInfo;
    }
    
    public void setTypeMappingInfo(TypeMappingInfo info) {
        this.typeMappingInfo = info;
    }

	public String getXmlMimeType() {
		return xmlMimeType;
	}

	public void setXmlMimeType(String xmlMimeType) {
		this.xmlMimeType = xmlMimeType;
	}

	public boolean isXmlAttachmentRef() {
		return xmlAttachmentRef;
	}

	public void setXmlAttachmentRef(boolean xmlAttachmentRef) {
		this.xmlAttachmentRef = xmlAttachmentRef;
	}

}
