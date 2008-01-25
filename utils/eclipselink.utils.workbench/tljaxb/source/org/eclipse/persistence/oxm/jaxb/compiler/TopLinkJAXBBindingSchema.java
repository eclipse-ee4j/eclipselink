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
import java.util.Vector;

/**
 *
 * @author    Rick Barkhouse <rick.barkhouse@oracle.com>
 * @since    04/15/2004 10:16:13
 */
public class TopLinkJAXBBindingSchema {
    private String namespace;
    private String fullNodeName;
    private String className;
    private String packageName;
    private String extendsNode;
    private boolean isInnerInterface;
    private String enclosingClassPackage;
    private Vector enclosingClassNames;
    private String simpleTypeName;
    private String implClassPackage;
    private ArrayList schemaBaseTypes;
    private ArrayList simpleContentTypes;
    private String simpleContentTypeName;
    private String typesafeEnumClassName;
    private String typesafeEnumPackage;
    private boolean isNillable;
    private TopLinkJAXBBindingSchema enclosingSchema;
    private String customizationName;

    // Child bindings
    private Vector properties;

    // Unmapped / Calculated attributes
    private String nodeName;
    private Vector innerInterfaces;
    public static final int COMPLEX_TYPE = 1;
    public static final int ELEMENT = 3;
    public static final int GROUP = 5;

    // ===========================================================================
    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String value) {
        this.namespace = value;
    }

    public String getFullNodeName() {
        return this.fullNodeName;
    }

    public void setFullNodeName(String value) {
        this.fullNodeName = value;
    }

    public String getClassName() {
        return this.className;
    }

    /**
     * Return the name attribute of the customization set on this schema's
     * binding, i.e. <orajaxb:customization name="class"/>
     *
     * @return customization name attribute
     */
    public String getCustomizationName() {
        return this.customizationName;
    }

    /**
     * Set the name attribute of the customization set on this schema's
     * binding, i.e. <orajaxb:customization name="class"/>
     *
     * @param value
     */
    public void setCustomizationName(String value) {
        this.customizationName = value;
    }

    /**
     * This convenience method indicates if the name attribute of the
     * customization set on this schema's binding is "class", i.e.
     * <orajaxb:customization name="class"/>
     *
     * @return true if the customization name="class", false otherwise
     */
    public boolean isClassCustomization() {
        if ((this.customizationName == null) || this.customizationName.equals("")) {
            return false;
        }

        return this.customizationName.equals("class");
    }

    public void setImplClassPackage(String p) {
        this.implClassPackage = p;
    }

    public String getImplClassPackage() {
        if (this.implClassPackage == null) {
            return getPackageName();
        }
        return this.implClassPackage;
    }

    public void setClassName(String value) {
        this.className = value;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getFullClassName() {
        return getFullClassName(false, false);
    }

    public String getFullClassName(boolean fullImpl, boolean dollarSign) {
        if (getEnclosingClassName() == null) {
            String packageName = "";
            if (!fullImpl || (this.implClassPackage == null) || this.implClassPackage.equals(getPackageName())) {
                packageName = getPackageName();
            } else {
                packageName = this.implClassPackage;
            }
            String fullClassName = packageName + "." + getClassName();
            if (fullImpl) {
                fullClassName += "Impl";
            }
            return fullClassName;
        }

        String fullClassName = this.getEnclosingClassName(dollarSign, fullImpl);
        if (fullImpl) {
            fullClassName += "$";
        } else if (dollarSign) {
            fullClassName += "$";
        } else {
            fullClassName += ".";
        }
        fullClassName += getClassName();
        if (fullImpl) {
            fullClassName += "Impl";
        }
        return fullClassName;
    }

    public void setPackageName(String value) {
        this.packageName = value;
    }

    public String getExtendsNode() {
        return this.extendsNode;
    }

    public void setExtendsNode(String value) {
        this.extendsNode = value;
    }

    public boolean getIsInnerInterface() {
        return this.isInnerInterface;
    }

    public void setIsInnerInterface(boolean value) {
        this.isInnerInterface = value;
    }

    public boolean getIsNillable() {
        return this.isNillable;
    }

    public void setIsNillable(boolean value) {
        this.isNillable = value;
    }

    public void setEnclosingClassName(Vector value) {
        this.enclosingClassNames = value;
    }

    public String getEnclosingClassName() {
        return getEnclosingClassName(false, false);
    }

    public String getEnclosingClassName(boolean dollarSign, boolean fullImpl) {
        if (!getIsInnerInterface()) {
            return null;
        }

        //build the enclosing class name
        Iterator iter = this.getEnclosingClassNames().iterator();
        String packageName = "";
        if (!fullImpl || (this.implClassPackage == null) || this.implClassPackage.equals(this.getEnclosingClassPackage())) {
            packageName = this.enclosingClassPackage;
        } else {
            packageName = this.implClassPackage;
        }
        String className = packageName + ".";
        while (iter.hasNext()) {
            String next = (String)iter.next();
            className += next;
            if (fullImpl) {
                className += "Impl";
            }
            if (iter.hasNext()) {
                if (fullImpl) {
                    className += "$";
                } else if (dollarSign) {
                    className += "$";
                } else {
                    className += ".";
                }
            }
        }
        return className;
    }

    public Vector getEnclosingClassNames() {
        return this.enclosingClassNames;
    }

    public String getEnclosingClassNameNoPackage() {
        if (!getIsInnerInterface()) {
            return null;
        }
        Iterator iter = this.getEnclosingClassNames().iterator();
        String className = "";
        while (iter.hasNext()) {
            String next = (String)iter.next();
            className += next;
            if (iter.hasNext()) {
                className += ".";
            }
        }
        return className;
    }

    public String getEnclosingClassPackage() {
        return this.enclosingClassPackage;
    }

    public void setEnclosingClassPackage(String value) {
        this.enclosingClassPackage = value;
    }

    public TopLinkJAXBBindingSchema getEnclosingSchema() {
        return this.enclosingSchema;
    }

    public void setEnclosingSchema(TopLinkJAXBBindingSchema value) {
        this.enclosingSchema = value;
    }

    public void setSimpleContentTypeName(String value) {
        this.simpleContentTypeName = value;
    }

    public String getSimpleContentTypeName() {
        return this.simpleContentTypeName;
    }

    /**
    * Set the list of schema types for 'simpleContentTypeName' as String objects.  Note that these types
    * will be converted to QNames when set on a mapping.
    *
    * @value A list of schema base types as Strings
    */
    public void setSimpleContentTypes(ArrayList value) {
        this.simpleContentTypes = value;
    }

    /**
    * Get the list of schema types for 'simpleContentTypeName' as String objects.  Note that these types
    * will be converted to QNames when set on a mapping.
    *
    * @return A list of schema types as Strings
    */
    public ArrayList getSimpleContentTypes() {
        return this.simpleContentTypes;
    }

    public String getSimpleTypeName() {
        return this.simpleTypeName;
    }

    public void setSimpleTypeName(String value) {
        this.simpleTypeName = value;
    }

    /**
    * Get the list of schema types for 'simpleTypeName' as String objects.  Note that these types
    * will be converted to QNames when set on a mapping.
    *
    * @return A list of schema types as Strings
    */
    public ArrayList getSchemaBaseTypes() {
        return this.schemaBaseTypes;
    }

    /**
    * Set the list of schema types for 'simpleTypeName' as String objects.  Note that these types
    * will be converted to QNames when set on a mapping.
    *
    * @value A list of schema base types as Strings
    */
    public void setSchemaBaseTypes(ArrayList value) {
        this.schemaBaseTypes = value;
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

    public boolean isTypesafeEnumeration() {
        if ((this.typesafeEnumClassName == null) || this.typesafeEnumClassName.equals("")) {
            return false;
        }
        return true;
    }

    public Vector getProperties() {
        return this.properties;
    }

    public void setProperties(Vector value) {
        this.properties = new Vector();
        java.util.Iterator it = value.iterator();
        while (it.hasNext()) {
            TopLinkJAXBProperty prop = (TopLinkJAXBProperty)it.next();
            if (prop.getName() != null) {
                this.properties.add(prop);
            }
        }
    }

    // ===========================================================================
    public String getNodeName() {
        if (this.nodeName == null) {
            stripNodeName();
        }
        return this.nodeName;
    }

    public Vector getInnerInterfaces() {
        if (this.innerInterfaces == null) {
            this.innerInterfaces = new Vector();
        }
        return this.innerInterfaces;
    }

    // ===========================================================================
    public boolean isComplexType() {
        return (this.fullNodeName.lastIndexOf("xs:complexType") != -1);
    }

    public boolean isElement() {
        return (this.fullNodeName.lastIndexOf("xs:element") != -1);
    }

    public boolean isGroup() {
        return (this.fullNodeName.lastIndexOf("xs:group") != -1);
    }

    // ===========================================================================
    public int getType() {
        if (isComplexType()) {
            return COMPLEX_TYPE;
        } else if (isElement()) {
            return ELEMENT;
        } else if (isGroup()) {
            return GROUP;
        } else {
            return -999;
        }
    }

    // ===========================================================================
    public void stripNodeName() {
        // nodeName is stored in the following format in the Default Customization File:
        //    .//xs:element[@name='employee']
        // We really only want the @name part, so strip the rest out.
        if ((this.fullNodeName != null) && (this.fullNodeName.lastIndexOf("@name='") != -1)) {
            int startIndex = this.fullNodeName.lastIndexOf("@name='") + 7;
            int endIndex = this.fullNodeName.lastIndexOf("'", this.fullNodeName.length());
            this.nodeName = this.fullNodeName.substring(startIndex, endIndex);
        }
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
        toString += (CR + tab + "   namespace:                " + getNamespace());
        toString += (CR + tab + "   nodeName:                 " + getNodeName());
        toString += (CR + tab + "   className:                " + getClassName());
        toString += (CR + tab + "   packageName:              " + getPackageName());
        toString += (CR + tab + "   extendsNode:              " + getExtendsNode());
        toString += (CR + tab + "   isInnerInterface:         " + getIsInnerInterface());
        toString += (CR + tab + "   customizationName:        " + getCustomizationName());
        toString += (CR + tab + "   enclosingClassName:       " + getEnclosingClassName());
        toString += (CR + tab + "   innerInterfaces:          " + getInnerInterfaces().size());
        toString += (CR + tab + "   simpleTypeName:           " + getSimpleTypeName());
        toString += (CR + tab + "   schemaBaseTypes:          " + this.schemaBaseTypes.toString());
        toString += (CR + tab + "   simpleContentTypeName:    " + getSimpleContentTypeName());
        toString += (CR + tab + "   simpleContentTypes:       " + this.simpleContentTypes.toString());
        toString += (CR + tab + "   properties:" + CR);
        for (int i = 0; i < getProperties().size(); i++) {
            TopLinkJAXBProperty property = (TopLinkJAXBProperty)getProperties().elementAt(i);
            toString += property.dump(indent + 2);
        }

        return toString;
    }

    private static final String CR = System.getProperty("line.separator");
}