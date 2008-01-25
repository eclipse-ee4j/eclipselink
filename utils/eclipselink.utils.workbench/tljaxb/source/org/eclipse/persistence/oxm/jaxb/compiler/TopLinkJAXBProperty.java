/*******************************************************************************
* Copyright (c) 2007 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0, which accompanies this distribution and is available at
* http://www.eclipse.org/legal/epl-v10.html.
*
* Contributors:
*     Oracle - initial API and implementation
******************************************************************************/
package org.eclipse.persistence.oxm.jaxb.compiler;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author    Rick Barkhouse <rick.barkhouse@oracle.com>
 * @since    04/15/2004 10:16:13
 */
public class TopLinkJAXBProperty {
    private String name;
    private String javaTypeName;
    private boolean isInnerInterface;
    private String collectionTypeName;
    private String nodeType;
    private ArrayList xmlTypes;
    private String namespace;
    private String strippedNodeType;
    private String mappingName;
    private String defaultValue;
    private boolean generateIsSetMethod;
    private String typesafeEnumClassName;
    private String typesafeEnumPackage;

    // ===========================================================================
    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String value) {
        this.defaultValue = value;
    }

    public boolean getIsInnerInterface() {
        return this.isInnerInterface;
    }

    public void setIsInnerInterface(boolean value) {
        this.isInnerInterface = value;
    }

    public boolean getGenerateIsSetMethod() {
        return this.generateIsSetMethod;
    }

    public void setGenerateIsSetMethod(boolean value) {
        this.generateIsSetMethod = value;
    }

    public String getCollectionTypeName() {
        return this.collectionTypeName;
    }

    public void setCollectionTypeName(String value) {
        this.collectionTypeName = value;
    }

    public String getNodeType() {
        return this.nodeType;
    }

    public void setNodeType(String type) {
        this.nodeType = type;
        setStrippedNodeType();
        setMappingName();
    }

    public String getJavaTypeName() {
        return this.javaTypeName;
    }

    public void setJavaTypeName(String value) {
        this.javaTypeName = value;
    }

    public String getTypesafeEnumPackage() {
        return this.typesafeEnumPackage;
    }

    public void setTypesafeEnumPackage(String value) {
        this.typesafeEnumPackage = value;
    }

    public String getTypesafeEnumClassName() {
        return this.typesafeEnumClassName;
    }

    public void setTypesafeEnumClassName(String value) {
        this.typesafeEnumClassName = value;
    }

    /**
     * Get the list of QNames representing the available schema types for this property.
     *
     * @return A list a javax.xml.namespace.QName objects
     */
    public ArrayList getXMLTypes() {
        return this.xmlTypes;
    }

    /**
     * Set the list of QNames representing the available schema types for this property.
     *
     * @param value A list a javax.xml.namespace.QName objects
     */
    public void setXMLTypes(ArrayList value) {
        this.xmlTypes = value;
    }

    private String getArrayListAsString(ArrayList list) {
        String type = "";
        if (list != null) {
            Iterator typeIt = list.iterator();

            while (typeIt.hasNext()) {
                type += (String)typeIt.next();
                type += " ";
            }
        }
        return type;
    }

    // ===========================================================================
    public boolean isCollectionType() {
        return this.collectionTypeName != null;
    }

    public boolean isComplexType() {
        return (getNodeType().lastIndexOf("xs:complexType") != -1);
    }

    public boolean isElement() {
        return (getNodeType().lastIndexOf("xs:element") != -1);
    }

    public boolean isAttribute() {
        return (getNodeType().lastIndexOf("xs:attribute") != -1);
    }

    public boolean isGroup() {
        return getNodeType().lastIndexOf("xs:group") != -1;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String value) {
        this.namespace = value;
    }

    private void setStrippedNodeType() {
        if ((this.nodeType != null) && (this.nodeType.lastIndexOf(".//xs:") != -1)) {
            int startIndex = this.nodeType.indexOf(".//xs:") + 6;
            int endIndex = this.nodeType.indexOf("[");
            this.strippedNodeType = this.nodeType.substring(startIndex, endIndex);
        } else {
            this.strippedNodeType = "";
        }
    }

    public String getStrippedNodeType() {
        return this.strippedNodeType;
    }

    private void setMappingName() {
        if ((this.nodeType != null) && (this.nodeType.lastIndexOf("@name=") != -1)) {
            int startIndex = this.nodeType.indexOf("@name='") + 7;
            int endIndex = this.nodeType.indexOf("']");
            this.mappingName = this.nodeType.substring(startIndex, endIndex);
        } else {
            this.mappingName = "";
        }
    }

    public String getMappingName() {
        return this.mappingName;
    }

    // ===========================================================================
    public String dump() {
        return dump(0);
    }

    public String dump(int indent) {
        String tab = "";
        for (int i = 0; i < indent; i++) {
            tab += "   ";
        }

        String toString = tab + super.toString() + ":";
        toString += (CR + tab + "   name:                 " + getName());
        toString += (CR + tab + "   nodeType:             " + getStrippedNodeType());
        toString += (CR + tab + "   javaTypeName:         " + getJavaTypeName());
        toString += (CR + tab + "   xmlTypes:             " + this.xmlTypes.toString());
        toString += (CR + tab + "   isInnerInterface:     " + getIsInnerInterface());
        toString += (CR + tab + "   collectionTypeName:   " + getCollectionTypeName());
        toString += (CR + tab + "   defaultValue:   		" + getDefaultValue());
        toString += (CR + tab + "   generateIsSetMethod:  " + getGenerateIsSetMethod());
        toString += CR;

        return toString;
    }

    private static final String CR = System.getProperty("line.separator");
}