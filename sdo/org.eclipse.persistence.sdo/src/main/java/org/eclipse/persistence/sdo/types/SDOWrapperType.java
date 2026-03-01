/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// rbarkhouse - May 26 2008 - 1.0M8 - Initial implementation
// rbarkhouse - July 14 2008 - 1.1 - Modified to enable wrappers to have multiple associated QNames

package org.eclipse.persistence.sdo.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.eclipse.persistence.sdo.SDOException;
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
        this(aPropertyType, aTypeName, aSDOTypeHelper, new QName[] { aSchemaType }, (Class<? extends SDODataObject>[]) new Class<?>[] { implClass });
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

        descriptorsMap = new HashMap<>();
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
                } catch (ClassNotFoundException | SecurityException e) {
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
            nsr.setDefaultNamespaceURI(javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
            XMLField field = new XMLField();
            field.setNamespaceResolver(nsr);
            field.setXPath("@" + javax.xml.XMLConstants.XMLNS_ATTRIBUTE);
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

    @Override
    public List getAliasNames() {
        return Collections.emptyList();
    }

    @Override
    public List getBaseTypes() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return typeName;
    }

    @Override
    public String getURI() {
        return SDOConstants.ORACLE_SDO_URL;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isDataType() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isSequenced() {
        return false;
    }

    @Override
    public boolean isFinalized() {
        return true;
    }

    @Override
    public boolean isWrapperType() {
        return true;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()));
        str.append("{uri=");
        str.append(getURI());
        str.append(" name=");
        str.append(getName());
        str.append("}");

        return str.toString();
    }

    /**
     * Wrapper for Boolean Object datatype.
     */
    public static class BooleanObjectWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public BooleanObjectWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Boolean datatype.
     */
    public static class BooleanWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public BooleanWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Byte Object datatype.
     */
    public static class ByteObjectWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public ByteObjectWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Bytes datatype.
     */
    public static class BytesWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public BytesWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Byte datatype.
     */
    public static class ByteWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public ByteWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for HEX Binary datatype.
     */
    public static class Bytes_hexBunaryWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public Bytes_hexBunaryWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Character Object datatype.
     */
    public static class CharacterObjectWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public CharacterObjectWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Character datatype.
     */
    public static class CharacterWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public CharacterWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Date Time datatype.
     */
    public static class DateTimeWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public DateTimeWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Date datatype.
     */
    public static class DateWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public DateWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Day datatype.
     */
    public static class DayWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public DayWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Decimal datatype.
     */
    public static class DecimalWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public DecimalWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Double Object datatype.
     */
    public static class DoubleObjectWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public DoubleObjectWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Double datatype.
     */
    public static class DoubleWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public DoubleWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Duration datatype.
     */
    public static class DurationWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public DurationWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Float Object datatype.
     */
    public static class FloatObjectWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public FloatObjectWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for FLoat datatype.
     */
    public static class FloatWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public FloatWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Integer datatype.
     */
    public static class IntegerWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public IntegerWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Int Object datatype.
     */
    public static class IntObjectWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public IntObjectWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Int datatype.
     */
    public static class IntWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public IntWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Long Object datatype.
     */
    public static class LongObjectWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public LongObjectWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Long datatype.
     */
    public static class LongWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public LongWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Month Day datatype.
     */
    public static class MonthDayWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public MonthDayWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Month datatype.
     */
    public static class MonthWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public MonthWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Object datatype.
     */
    public static class ObjectWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public ObjectWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Short object datatype.
     */
    public static class ShortObjectWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public ShortObjectWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Short datatype.
     */
    public static class ShortWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public ShortWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Strings datatype.
     */
    public static class StringsWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public StringsWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for String datatype.
     */
    public static class StringWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public StringWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Time datatype.
     */
    public static class TimeWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public TimeWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for URI datatype.
     */
    public static class URIWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public URIWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for URI QName datatype.
     */
    public static class URI_QNameWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public URI_QNameWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for YearMonthDay datatype.
     */
    public static class YearMonthDayWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public YearMonthDayWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for YearMonth datatype.
     */
    public static class YearMonthWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public YearMonthWrapperImpl() {
            super();
        }
    }

    /**
     * Wrapper for Year datatype.
     */
    public static class YearWrapperImpl extends SDODataObject {
        /**
         * Default constructor.
         */
        public YearWrapperImpl() {
            super();
        }
    }

}
