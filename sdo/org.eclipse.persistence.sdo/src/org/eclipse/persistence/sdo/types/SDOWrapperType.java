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
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLTransformationMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.sdo.SDOConstants;
import org.eclipse.persistence.sdo.SDODataObject;
import org.eclipse.persistence.sdo.SDOProperty;
import org.eclipse.persistence.sdo.SDOType;
import org.eclipse.persistence.sdo.helper.SDOClassLoader;
import org.eclipse.persistence.sdo.helper.SDOMethodAttributeAccessor;
import org.eclipse.persistence.sdo.helper.SDOTypeHelper;
import org.eclipse.persistence.sdo.helper.SDOXMLHelper;
import org.eclipse.persistence.sdo.helper.extension.SDOUtil;
import org.eclipse.persistence.sdo.helper.metadata.NamespaceURITransformer;
import org.eclipse.persistence.sdo.helper.metadata.QNameTransformer;
import org.eclipse.persistence.sessions.Project;

import commonj.sdo.Type;

/**
 * Wrapper for XML datatypes.
 * @author rbarkhou
 */
public class SDOWrapperType extends SDOType implements Type {

    private static final String PACKAGE_NAME = "org.eclipse.persistence.sdo.dataobjects.";
    private static final String ATTRIBUTE_NAME = "value";
    private static final String XPATH = "text()";

    private String typeName;
    private Map<QName, XMLDescriptor> descriptorsMap;

    public SDOWrapperType(Type aPropertyType, String aTypeName, SDOTypeHelper aSDOTypeHelper) {
        this(aPropertyType, aTypeName, aSDOTypeHelper, (QName) null, null);
    }

    public SDOWrapperType(Type aPropertyType, String aTypeName, SDOTypeHelper aSDOTypeHelper, QName aSchemaType) {
        this(aPropertyType, aTypeName, aSDOTypeHelper, new QName[] { aSchemaType }, null);
    }

    public SDOWrapperType(Type aPropertyType, String aTypeName, SDOTypeHelper aSDOTypeHelper, QName aSchemaType, Class<? extends SDODataObject> implClass) {
        this(aPropertyType, aTypeName, aSDOTypeHelper, new QName[] { aSchemaType }, new Class[] { implClass });
    }

    public SDOWrapperType(Type aPropertyType, String aTypeName, SDOTypeHelper aSDOTypeHelper, QName[] schemaTypes) {
        this(aPropertyType, aTypeName, aSDOTypeHelper, schemaTypes, null);
    }

    public SDOWrapperType(Type aPropertyType, String aTypeName, SDOTypeHelper aSDOTypeHelper, QName[] schemaTypes, Class<? extends SDODataObject>[] implClasses) {
        super(SDOConstants.ORACLE_SDO_URL, aTypeName, aSDOTypeHelper);
        typeName = aTypeName;

        SDOProperty valueProperty = new SDOProperty(aHelperContext);
        valueProperty.setName("value");
        valueProperty.setType(aPropertyType);
        valueProperty.setXsdType(schemaTypes[0]);
        addDeclaredProperty(valueProperty);

        String normalizedTypeName = SDOUtil.className(aTypeName, true, true, false);
        if(null == implClasses || null == implClasses[0]) {
            // Remove any special characters from the type name to create the class name:
            String implClassName = PACKAGE_NAME + normalizedTypeName + "WrapperImpl" ;
            setImplClassName(implClassName);
            getImplClass();
        } else {
            this.javaImplClass = implClasses[0]; 
            this.xmlDescriptor.setJavaClass(javaImplClass);
        }

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
                String className = PACKAGE_NAME + normalizedTypeName + "_" + schemaType.getLocalPart() + "Wrapper";

                // Now generate the class in memory
                try {
                    if(null == implClasses || null == implClasses[i]) {
                        SDOClassLoader loader = ((SDOXMLHelper)aHelperContext.getXMLHelper()).getLoader();
                        d.setJavaClass(loader.loadClass(className + "Impl", this));
                    } else {
                        d.setJavaClass(implClasses[i]);
                    }
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

        SDOMethodAttributeAccessor accessor = null;
        accessor = new SDOMethodAttributeAccessor(aValueProperty);

        if (XMLConstants.QNAME_QNAME.equals(aQName)) {
            XMLTransformationMapping mapping = new XMLTransformationMapping();
            mapping.setAttributeName(ATTRIBUTE_NAME);

            QNameTransformer transformer = new QNameTransformer("text()");
            mapping.setAttributeTransformer(transformer);
            mapping.addFieldTransformer(XPATH, transformer);

            NamespaceResolver nsr = new NamespaceResolver();
            nsr.setDefaultNamespaceURI(XMLConstants.XMLNS_URL);
            XMLField field = new XMLField();
            field.setNamespaceResolver(nsr);
            field.setXPath("@" + XMLConstants.XMLNS);
            mapping.addFieldTransformer(field, new NamespaceURITransformer());

            mapping.setAttributeAccessor(accessor);
            aDescriptor.addMapping(mapping);
        } else {
            XMLDirectMapping mapping = new XMLDirectMapping();
            mapping.setAttributeName(ATTRIBUTE_NAME);
            mapping.setXPath(XPATH);

            mapping.setAttributeClassification(aPropertyType.getInstanceClass());

            ((XMLField) mapping.getField()).setSchemaType(aQName);

            mapping.setAttributeAccessor(accessor);
            aDescriptor.addMapping(mapping);
        }

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

    public static class BooleanObjectWrapperImpl extends SDODataObject {}
    public static class BooleanWrapperImpl extends SDODataObject {}
    public static class ByteObjectWrapperImpl extends SDODataObject {}
    public static class BytesWrapperImpl extends SDODataObject {}
    public static class ByteWrapperImpl extends SDODataObject {}
    public static class Bytes_hexBunaryWrapperImpl extends SDODataObject {};
    public static class CharacterObjectWrapperImpl extends SDODataObject {}
    public static class CharacterWrapperImpl extends SDODataObject {}
    public static class DateTimeWrapperImpl extends SDODataObject {}
    public static class DateWrapperImpl extends SDODataObject {}
    public static class DayWrapperImpl extends SDODataObject {}
    public static class DecimalWrapperImpl extends SDODataObject {}
    public static class DoubleObjectWrapperImpl extends SDODataObject {}
    public static class DoubleWrapperImpl extends SDODataObject {}
    public static class DurationWrapperImpl extends SDODataObject {}
    public static class FloatObjectWrapperImpl extends SDODataObject {}
    public static class FloatWrapperImpl extends SDODataObject {}
    public static class IntegerWrapperImpl extends SDODataObject {}
    public static class IntObjectWrapperImpl extends SDODataObject {}
    public static class IntWrapperImpl extends SDODataObject {}
    public static class LongObjectWrapperImpl extends SDODataObject {}
    public static class LongWrapperImpl extends SDODataObject {}
    public static class MonthDayWrapperImpl extends SDODataObject {}
    public static class MonthWrapperImpl extends SDODataObject {}
    public static class ObjectWrapperImpl extends SDODataObject {}
    public static class ShortObjectWrapperImpl extends SDODataObject {}
    public static class ShortWrapperImpl extends SDODataObject {}
    public static class StringsWrapperImpl extends SDODataObject {}
    public static class StringWrapperImpl extends SDODataObject {}
    public static class TimeWrapperImpl extends SDODataObject {}
    public static class URIWrapperImpl extends SDODataObject {}
    public static class URI_QNameWrapperImpl extends SDODataObject {}
    public static class YearMonthDayWrapperImpl extends SDODataObject {}
    public static class YearMonthWrapperImpl extends SDODataObject {}
    public static class YearWrapperImpl extends SDODataObject {}

}