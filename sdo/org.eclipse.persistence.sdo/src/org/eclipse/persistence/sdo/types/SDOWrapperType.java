/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
* This program and the accompanying materials are made available under the terms
* of the Eclipse Public License v1.0 and Eclipse Distribution License v1.0
* which accompanies this distribution.
*
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
* rbarkhouse - May 26 2008 - 1.0M8 - Initial implementation
* rbarkhouse - July 14 2008 - 1.1 - Modified to enable wrappers to have multiple associated QNames
******************************************************************************/

package org.eclipse.persistence.sdo.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.persistence.exceptions.SDOException;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOClassLoader;
import org.eclipse.persistence.sdo.helper.SDOMethodAttributeAccessor;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.extension.SDOUtil;
import org.eclipse.persistence.sessions.Project;

import commonj.sdo.Type;

/**
 * Wrapper for XML datatypes.
 * @author rbarkhou
 */
public class SDOWrapperType extends SDOType implements Type {

    private String typeName;
    private Map<QName, XMLDescriptor> descriptorsMap;

    public SDOWrapperType(Type aPropertyType, String aTypeName, SDOTypeHelper aSDOTypeHelper) {
        this(aPropertyType, aTypeName, aSDOTypeHelper, (QName) null);
    }

    public SDOWrapperType(Type aPropertyType, String aTypeName, SDOTypeHelper aSDOTypeHelper, QName aSchemaType) {
        this(aPropertyType, aTypeName, aSDOTypeHelper, new QName[] { aSchemaType });
    }

    public SDOWrapperType(Type aPropertyType, String aTypeName, SDOTypeHelper aSDOTypeHelper, QName[] schemaTypes) {
        super(SDOConstants.ORACLE_SDO_URL, aTypeName, aSDOTypeHelper);
        typeName = aTypeName;

        SDOProperty valueProperty = new SDOProperty(aHelperContext);
        valueProperty.setName("value");
        valueProperty.setType(aPropertyType);
        valueProperty.setXsdType(schemaTypes[0]);
        addDeclaredProperty(valueProperty);

        // Remove any special characters from the type name to create the class name:
        String normalizedTypeName = SDOUtil.className(aTypeName, true, true, false);
        String implClassName = "org.eclipse.persistence.sdo." + normalizedTypeName + "WrapperImpl" ;
        setImplClassName(implClassName);

        getImplClass();

        // Add a new map to typehelperdelegate
        // interface --> sdotype
        // add (this) into map during constructor i.e. here
        // change getType(Class) on typehelperdelegate to check map first

        initializeDescriptor(xmlDescriptor, schemaTypes[0], aPropertyType, valueProperty);
        
        descriptorsMap = new HashMap<QName, XMLDescriptor>();
        descriptorsMap.put(schemaTypes[0], xmlDescriptor);
        setSchemaContext(xmlDescriptor, schemaTypes[0]);

        if (schemaTypes.length > 1) {
            for (int i = 1; i < schemaTypes.length; i++) {
                XMLDescriptor d = new XMLDescriptor();
                QName schemaType = schemaTypes[i];
                String className = "org.eclipse.persistence.sdo." + normalizedTypeName + "_" + schemaType.getLocalPart() + "Wrapper";

                // Now generate the class in memory
                try {
                    SDOClassLoader loader = ((SDOXMLHelper)aHelperContext.getXMLHelper()).getLoader();
                    d.setJavaClass(loader.loadClass(className + "Impl", this));
                } catch (ClassNotFoundException e) {
                    throw SDOException.classNotFound(e, getURI(), getName());
                } catch (SecurityException e) {
                    throw SDOException.classNotFound(e, getURI(), getName());
                }

                initializeDescriptor(d, schemaType, aPropertyType, valueProperty);
                descriptorsMap.put(schemaTypes[i], d);
                setSchemaContext(d, schemaTypes[i]);
            }
        }
    }

    /**
     * Convenience method that sets a schema context (as QName) on a given descriptor.
     * 
     * If either schemaType or desc is null, no action is performed.
     * 
     * @param desc XML descriptor to which an XMLSchemaReference will be added
     * @param schemaType QName that will be set as the schema context QName
     */
    private void setSchemaContext(XMLDescriptor desc, QName schemaType) {
        if (schemaType == null || desc == null) {
            return;
        }
        XMLSchemaURLReference urlRef = new XMLSchemaURLReference();
        urlRef.setSchemaContextAsQName(schemaType);
        desc.setSchemaReference(urlRef);
    }

    private void initializeDescriptor(XMLDescriptor aDescriptor, QName aQName, Type aPropertyType, SDOProperty aValueProperty) {
        aDescriptor.setNamespaceResolver(null);

        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("value");
        mapping.setXPath("text()");

        mapping.setAttributeClassification(aPropertyType.getInstanceClass());

        ((XMLField) mapping.getField()).setSchemaType(aQName);

        SDOMethodAttributeAccessor accessor = null;
        accessor = new SDOMethodAttributeAccessor(aValueProperty);
        mapping.setAttributeAccessor(accessor);

        aDescriptor.addMapping(mapping);

        aDescriptor.setIsWrapper(true);
    }

    public XMLDescriptor getXmlDescriptor(QName aQName) {
        XMLDescriptor d = descriptorsMap.get(aQName);

        if (d == null) {
            // Return the default
            return xmlDescriptor;
        }

        return d;
    }

    public Map getDescriptorsMap() {
        return descriptorsMap;
    }

    public void addDescriptorToProject(Project project) {
        Iterator<XMLDescriptor> it = descriptorsMap.values().iterator();
        while (it.hasNext()) {
            project.addDescriptor(it.next());
        }
    }

    public List getAliasNames() {
        return Collections.EMPTY_LIST;
    }

    public List getBaseTypes() {
        return Collections.EMPTY_LIST;
    }

    public String getName() {
        return typeName;
    }

    public String getURI() {
        return SDOConstants.ORACLE_SDO_URL;
    }

    public boolean isAbstract() {
        return false;
    }

    public boolean isDataType() {
        return false;
    }

    public boolean isOpen() {
        return false;
    }

    public boolean isSequenced() {
        return false;
    }

    public boolean isFinalized() {
        return true;
    }

    public boolean isWrapperType() {
        return true;
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append(getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()));
        str.append("{uri=");
        str.append(getURI());
        str.append(" name=");
        str.append(getName());
        str.append("}");

        return str.toString();
    }
}