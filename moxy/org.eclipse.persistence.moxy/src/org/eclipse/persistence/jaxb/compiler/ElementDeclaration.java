/*******************************************************************************
* Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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

/**
 * <p>An ElementDeclaration object is used to represent the information that is
 * associated with a global element in XML.</p> 
 * 
 * <p>ElementDeclarations will be created for classes with an XMLRootElement annotation,
 * for TypeMappingInfo objects which have an xml tag name specified.</p>
 */
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
    private boolean nillable;
    /**
     * Create a new ElementDeclaration.  By default the scope of this ElementDeclaration 
     * will be XmlElementDecl.GLOBAL
     * 
     * @param name The QName of this element
     * @param javaType The JavaClass of this element 
     * @param javaTypeName The String name of the javaType
     * @param isList A boolean representing if this corresponds to an xsd:list 
     */
    public ElementDeclaration(QName name, JavaClass javaType, String javaTypeName, boolean isList) {
        this.elementName = name;
        this.javaTypeName = javaTypeName;
        this.javaType = javaType;
        this.substitutableElements = new ArrayList<ElementDeclaration>();
        this.isList = isList;
        this.scopeClass = XmlElementDecl.GLOBAL.class;
    }
    
    /**
     * Create a new ElementDeclaration and set the scope.
     * @param name The QName of this element
     * @param javaType The JavaClass of this element 
     * @param javaTypeName The String name of the javaType
     * @param isList A boolean representing if this corresponds to an xsd:list
     * @param scopeClass The class representing the scope of this element
     */
    public ElementDeclaration(QName name, JavaClass javaType, String javaTypeName, boolean isList, Class scopeClass) {
    	this(name, javaType, javaTypeName, isList);
    	this.scopeClass = scopeClass;        
    }
    
    /**
     * Get the QName representing this element
     * @return the QName associated with this element.
     */
    public QName getElementName() {
        return elementName;
    }
    
    /**
     * Get the name of the java type associated with this global element.
     * This may be set through the constructor or will be set when setJavaType(JavaClass) is called.
     * @return the name of the java type that corresponds to this element
     */
    public String getJavaTypeName() {
        return javaTypeName;
    }
    
    /**
     * The list of elements which can be substituted for this element (ie: has this element in their substitutionGroup)
     * @return the list of element declarations which can be substituted for this element
     */
    public List<ElementDeclaration> getSubstitutableElements() {
        return substitutableElements;
    }

    /**
     * Add an element to the list of elements which can be substituted for this element (ie: has this element in their substitutionGroup)
     */
    public void addSubstitutableElement(ElementDeclaration element) {
        if (element != this && element != null) {
            QName elementName = element.getElementName();
            for (ElementDeclaration substitutableElement : substitutableElements) {
                if (substitutableElement.getElementName().equals(elementName)) {
                    return;
                }
            }
            this.substitutableElements.add(element);
        }
    }
    
    /**
     * If this element has a substitutionGroup this will be set.
     * @param rootElement the QName value of the substitutionGroup
     */
    public void setSubstitutionHead(QName rootElement) {
        this.substitutionHead = rootElement;
    }
    
    /**
     * If this element has a substitutionGroup this will be set.
     * @return the value of the substitutionGroup
     */
    public QName getSubstitutionHead() {
        return substitutionHead;
    }

    /**
     * Track if this element had an @XmlRootElement annotation
     * @return if the element has an @XmlRootElement
     */
    public boolean isXmlRootElement() {
        return this.isXmlRootElement;
    }
    
    /**
     * Mark if this element had an @XmlRootElement annotation
     * @param isXmlRoot if the element has an @XmlRootElement
     */
    public void setIsXmlRootElement(boolean isXmlRoot) {
        this.isXmlRootElement = isXmlRoot;
    }
    /**
     * Return if the global element will be marked as nillable
     * @return
     */
    public boolean isNillable() {
		return nillable;
	}

    /**
     * Set if the global element should be marked as nillable
     * @param nillable
     */
	public void setNillable(boolean nillable) {
		this.nillable = nillable;
	}
    
    
    /**
     * The javaType associated with this element.  Maybe set by the constructor
     * or by setJavaType.
     * @return the javaType associated with this element.
     */
    public JavaClass getJavaType() {
        return this.javaType;
    }
    

    /**
     * Set the javaType associated with this element.  
     * This will also set the java type name associated with this element to type.getQualifiedName()
     * @param type the javaType associated with this element.
     */
    public void setJavaType(JavaClass type) {
        this.javaType = type;
        this.javaTypeName = type.getQualifiedName();
    }

    /**
     * Return if this element is a list
     * @return isList true if the element is a list
     */
    public boolean isList() {
        return isList;
    }

    /**
     * Mark if this element is a list
     * @param isList true if the element is a list
     */
    public void setList(boolean isList) {
        this.isList = isList;
    }

    /**
     * Get the java type adapter class associated with the element 
     * @return the java type adapater class associated with this element. May return null.
     */
    public Class getJavaTypeAdapterClass() {
        return javaTypeAdapterClass;
    }

    /**
     * Set the java type adapter class associated with this element if applicable.
     * @param javaTypeAdapterClass Class of the java type adapter associated with this element.
     */
    public void setJavaTypeAdapterClass(Class javaTypeAdapterClass) {
        this.javaTypeAdapterClass = javaTypeAdapterClass;
    }

    /**
     * Get the adapted java type.  
     * Only set when an XmlJavaTypeAdapter is present.
     * @return the JavaClass of the adapted java type.  May return null.
     */
    public JavaClass getAdaptedJavaType() {
        return adaptedJavaType;
    }

    /**
     * Set the adapted java type if there is an XmlJavaTypeAdapter associated with this element
     * This will also set the adaptedJavaTypeName (getAdaptedJavaTypeName)
     * @param adaptedJavaType set the JavaClass representing the adapted Java type
     */
    public void setAdaptedJavaType(JavaClass adaptedJavaType) {
        this.adaptedJavaType = adaptedJavaType;
        this.adaptedJavaTypeName = adaptedJavaType.getQualifiedName();
    }

    /**
     * Get the adapted java type name.  
     * Only set when an XmlJavaTypeAdapter is present.
     * Will be set to adaptedJavaType.getQualifiedName when setAdaptedJavaType is called
     * @return the name of the class of the adapted java type.  May return null.
     */
    public String getAdaptedJavaTypeName() {
        return adaptedJavaTypeName;
    }

    /**
     * Get the scope class associated with this element.
     * By default the scope of this ElementDeclaration 
     * will be XmlElementDecl.GLOBAL
     * @return the scope class associated with this element
     */
    public Class getScopeClass() {
        return scopeClass;
    }
    
    /**   
     * Set the scope class associated with this element.
     * Default setting is XmlElementDecl.GLOBAL
     * @param scopeClass associated with this element.
     */  
    public void setScopeClass(Class scopeClass) {
        this.scopeClass = scopeClass;
    }
    
    /**
     * This will be set if XmlElementDecl has a defaultValue specified
     * @return the default value associated with this element.  May return null.
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    /**
     * Set the default value associated with this element. 
     * @param value the default value that corresponds to this element.
     */
    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }
    
    /**
     * Get the TypeMappingInfo object if this ElementDeclaration was created from a TypeMappingInfo
     * @return the corresponding TypeMappingInfo.  May return null.
     */
    public TypeMappingInfo getTypeMappingInfo() {
        return this.typeMappingInfo;
    }
    
    /**
     * Set the TypeMappingInfo object if this ElementDeclaration was created from a TypeMappingInfo
     * @param The TypeMappingInfo object used to create this ElementDeclaration
     */
    public void setTypeMappingInfo(TypeMappingInfo info) {
        this.typeMappingInfo = info;
    }

    /**
     * Return the mimeType specified on this element.
     * @return the mimeType specified on this element. May return null.
     */
	public String getXmlMimeType() {
		return xmlMimeType;
	}

	/**
	 * Set of this element has an XmlMimeType annotation
	 * @param xmlMimeType set the name of the mime type if specified for this element
	 */
	public void setXmlMimeType(String xmlMimeType) {
		this.xmlMimeType = xmlMimeType;
	}

	/**
	 * Return if this element is associated with an XmlAttachmentRef annotation 
	 * @return if this element is associated with an XmlAttachmentRef annotation
	 */
	public boolean isXmlAttachmentRef() {
		return xmlAttachmentRef;
	}

	/**
	 * Set if there is an XmlAttachmentRef annotations associated with this element.
	 * @param xmlAttachmentRef true if there is an XmlAttachmentRef annotation
	 */
	public void setXmlAttachmentRef(boolean xmlAttachmentRef) {
		this.xmlAttachmentRef = xmlAttachmentRef;
	}

}
