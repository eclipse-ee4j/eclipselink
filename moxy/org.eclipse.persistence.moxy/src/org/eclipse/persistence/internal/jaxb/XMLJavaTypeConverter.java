/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jaxb;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.HashMap;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;

import org.eclipse.persistence.core.mappings.converters.CoreConverter;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.exceptions.JAXBException;
import org.eclipse.persistence.internal.oxm.XMLBinaryDataHelper;
import org.eclipse.persistence.internal.oxm.XMLConversionManager;
import org.eclipse.persistence.internal.oxm.mappings.BinaryDataMapping;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.ObjectTypeConverter;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.sessions.Session;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
/**
 * Converter that wraps an XmlAdapter.
 *
 * @see javax.xml.bind.annotation.adapters.XmlAdapter
 */
public class XMLJavaTypeConverter extends org.eclipse.persistence.oxm.mappings.converters.XMLConverterAdapter {
    protected Class<?> boundType = Object.class;
    protected Class<?> valueType = Object.class;
    protected Class<? extends XmlAdapter<?,?>> xmlAdapterClass;
    protected String xmlAdapterClassName;
    protected XmlAdapter<?,?> xmlAdapter;
    protected QName schemaType;
    protected DatabaseMapping mapping;
    protected CoreConverter<DatabaseMapping, Session> nestedConverter;

    /**
     * The default constructor.  This constructor should be used
     * in conjunction with the setXmlAdapterClass method or
     * setXmlAdapterClassName method.
     */
    public XMLJavaTypeConverter() {
    }

    /**
     * This constructor takes the XmlAdapter class to be used with this
     * converter.
     *
     * @param xmlAdapterClass
     */
    @SuppressWarnings("unchecked")
    public XMLJavaTypeConverter(Class<?> xmlAdapterClass) {
        setXmlAdapterClass((Class<XmlAdapter<?,?>>) xmlAdapterClass);
    }

    /**
     * This constructor takes an adapter class name.  The adapter
     * class will be loaded during initialization.
     *
     * @param xmlAdapterClassName
     */
    public XMLJavaTypeConverter(String xmlAdapterClassName) {
        this.xmlAdapterClassName = xmlAdapterClassName;
    }

    /**
     * This constructor takes the XmlAdapter class to be used with this
     * converter, as well as a schema type to be used during the conversion
     * operation.  During unmarshal, the value type will be converted to
     * the schema type, then from that type to the bound type.  The opposite
     * will occur during marshal.
     *
     * @param xmlAdapterClass
     * @param schemaType
     */
    @SuppressWarnings("unchecked")
    public XMLJavaTypeConverter(Class<?> xmlAdapterClass, QName schemaType) {
        setSchemaType(schemaType);
        setXmlAdapterClass((Class<XmlAdapter<?,?>>) xmlAdapterClass);
    }

    /**
     * This constructor takes the XmlAdapter class name to be used with this
     * converter (loaded during initialization), as well as a schema type to
     * be used during the conversion operation.  During unmarshal, the value
     * type will be converted to the schema type, then from that type to the
     * bound type.  The opposite will occur during marshal.
     *
     * @param xmlAdapterClassName
     * @param schemaType
     */
    public XMLJavaTypeConverter(String xmlAdapterClassName, QName schemaType) {
        setSchemaType(schemaType);
        setXmlAdapterClassName(xmlAdapterClassName);
    }

    /**
     * Wraps the XmlAdapter unmarshal method.
     */
    public Object convertDataValueToObjectValue(Object dataValue, Session session, XMLUnmarshaller unmarshaller) {
        try {
            XmlAdapter<?, ?> adapter = this.xmlAdapter;
            if (unmarshaller != null) {
                @SuppressWarnings("unchecked")
                HashMap<Class<XmlAdapter<?,?>>, XmlAdapter<?, ?>> adapters
                        = (HashMap<Class<XmlAdapter<?,?>>, XmlAdapter<?, ?>>) unmarshaller.getProperty(JAXBUnmarshaller.XML_JAVATYPE_ADAPTERS);
                if (adapters != null) {
                    XmlAdapter<?, ?> runtimeAdapter = adapters.get(this.xmlAdapterClass);
                    if (runtimeAdapter != null) {
                        adapter = runtimeAdapter;
                    }
                }
            }
            Object toConvert = dataValue;
            //apply nested converter first
            if(nestedConverter != null) {
                toConvert = nestedConverter.convertDataValueToObjectValue(toConvert, session);
            } else {
                if ((dataValue != null) && !(dataValue.getClass() == this.valueType)) {
                    if (this.mapping instanceof BinaryDataMapping) {
                        toConvert = XMLBinaryDataHelper.getXMLBinaryDataHelper().convertObject(dataValue, valueType, (AbstractSession) session, this.mapping.getContainerPolicy());
                    } else {
                        if (getSchemaType() != null) {
                            toConvert = ((XMLConversionManager) session.getDatasourcePlatform().getConversionManager()).convertObject(dataValue, valueType, getSchemaType());
                        } else {
                            toConvert = session.getDatasourcePlatform().getConversionManager().convertObject(dataValue, valueType);
                        }
                    }
                }
            }
            //noinspection unchecked
            return ((XmlAdapter<Object, ?>)adapter).unmarshal(toConvert);
        } catch (Exception ex) {
            if(unmarshaller == null || unmarshaller.getErrorHandler() == null){
                throw ConversionException.couldNotBeConverted(dataValue, boundType, ex);
            }
            try {
                unmarshaller.getErrorHandler().warning(new SAXParseException(null, null, ex));
                return null;
            } catch (SAXException e) {
                throw ConversionException.couldNotBeConverted(dataValue, boundType, ex);
            }
        }
    }

    /**
     * Wraps the XmlAdapter marshal method.
     */
    public Object convertObjectValueToDataValue(Object objectValue, Session session, XMLMarshaller marshaller) {
        try {
            XmlAdapter<?, ?> adapter = this.xmlAdapter;
            if (marshaller != null) {
                @SuppressWarnings("unchecked")
                HashMap<Class<XmlAdapter<?,?>>, XmlAdapter<?,?>> adapters
                        = (HashMap<Class<XmlAdapter<?,?>>, XmlAdapter<?,?>>) marshaller.getProperty(JAXBMarshaller.XML_JAVATYPE_ADAPTERS);
                if (adapters != null) {
                    XmlAdapter<?,?> runtimeAdapter = adapters.get(this.xmlAdapterClass);
                    if (runtimeAdapter != null) {
                        adapter = runtimeAdapter;
                    }
                }
            }
            @SuppressWarnings("unchecked")
            Object dataValue = ((XmlAdapter<?, Object>) adapter).marshal(objectValue);
            if(nestedConverter != null) {
                dataValue = nestedConverter.convertObjectValueToDataValue(dataValue, session);
            }
            return dataValue;
        } catch (Exception ex) {
            if(marshaller == null || marshaller.getErrorHandler() == null){
                throw ConversionException.couldNotBeConverted(objectValue, valueType, ex);
            }
            try {
                marshaller.getErrorHandler().warning(new SAXParseException(null, null, ex));
                return null;
            } catch (SAXException e) {
                throw ConversionException.couldNotBeConverted(objectValue, valueType, ex);
            }
        }
    }

    /**
     * Get the schema type to be used during conversion.
     */
    public QName getSchemaType() {
        return schemaType;
    }

    /**
     * Return the XmlAdapter class for this converter.
     *
     * @return xmlAdapterClass
     */
    public Class<? extends XmlAdapter<?,?>> getXmlAdapterClass() {
        return xmlAdapterClass;
    }

    /**
     * Return the XmlAdapter class name for this converter.
     * If null, the name will be set to "".
     *
     * @return xmlAdapterClassName
     */
    public String getXmlAdapterClassName() {
        if (xmlAdapterClassName == null) {
            xmlAdapterClassName = "";
        }
        return xmlAdapterClassName;
    }

    /**
     * Figure out the BoundType and ValueType for the XmlAdapter class, then
     * either create an instance of the XmlAdapter, or if an instance is set
     * on the marshaller, use it.
     *
     * @param mapping
     * @param session
     */
    public void initialize(DatabaseMapping mapping, Session session) {
        // if the adapter class is null, try the adapter class name
        ClassLoader loader = session.getDatasourceLogin().getDatasourcePlatform().getConversionManager().getLoader();
        if (xmlAdapterClass == null) {
            xmlAdapterClass = PrivilegedAccessHelper.callDoPrivilegedWithException(
                    () -> PrivilegedAccessHelper.getClassForName(getXmlAdapterClassName(), true, loader),
                    (ex) -> JAXBException.adapterClassNotLoaded(getXmlAdapterClassName(), ex)
            );
        }

        // validate adapter class extends javax.xml.bind.annotation.adapters.XmlAdapter
        if (!XmlAdapter.class.isAssignableFrom(xmlAdapterClass)) {
            throw JAXBException.invalidAdapterClass(getXmlAdapterClassName());
        }

        setBoundTypeAndValueTypeInCaseOfGenericXmlAdapter();
        try {
            try {
                xmlAdapter = PrivilegedAccessHelper.callDoPrivilegedWithException(
                        () -> PrivilegedAccessHelper.newInstanceFromClass(getXmlAdapterClass())
                );
            } catch (IllegalAccessException e) {
                Constructor<? extends XmlAdapter<?,?>> ctor = PrivilegedAccessHelper.callDoPrivilegedWithException(
                        () -> PrivilegedAccessHelper.getDeclaredConstructorFor(xmlAdapterClass, new Class<?>[0], true)
                );
                xmlAdapter = PrivilegedAccessHelper.invokeConstructor(ctor, new Object[0]);
            }
        } catch (Exception ex) {
            throw JAXBException.adapterClassCouldNotBeInstantiated(getXmlAdapterClassName(), ex);
        }
        if (nestedConverter != null) {
            if (nestedConverter instanceof ObjectTypeConverter) {
                ((ObjectTypeConverter) nestedConverter).convertClassNamesToClasses(loader);
            }
            nestedConverter.initialize(mapping, session);
        }
    }

    private void setBoundTypeAndValueTypeInCaseOfGenericXmlAdapter() {
        Type[] parameterizedTypeArguments = GenericsClassHelper.getParameterizedTypeArguments(xmlAdapterClass, XmlAdapter.class);

        if (null != parameterizedTypeArguments) {
            Class valueTypeClass = GenericsClassHelper.getClassOfType(parameterizedTypeArguments[0]);
            if (null != valueTypeClass) {
                valueType = valueTypeClass;
            }
            if (valueType.isInterface()) {
                valueType = Object.class; // during unmarshalling we'll need to instantiate this, so -> no interfaces
            }

            Class boundTypeClass = GenericsClassHelper.getClassOfType(parameterizedTypeArguments[1]);
            if (null != boundTypeClass) {
                boundType = boundTypeClass;
            }
        }
    }

    /**
     * Satisfy the interface.
     */
    public boolean isMutable() {
        return false;
    }

    /**
     * Set the schema type to be used during conversion - if one is
     * required.
     */
    public void setSchemaType(QName qname) {
        schemaType = qname;
    }

    /**
     * Set the XmlAdapter class to be used with this converter.
     *
     * @param xmlAdapterClass
     */
    public void setXmlAdapterClass(Class<? extends XmlAdapter<?,?>> xmlAdapterClass) {
        this.xmlAdapterClass = xmlAdapterClass;
    }

    /**
     * Set the XmlAdapter class to be used with this converter.
     *
     * @param xmlAdapterClassName
     */
    public void setXmlAdapterClassName(String xmlAdapterClassName) {
        this.xmlAdapterClassName = xmlAdapterClassName;
    }

    /**
     * Get the nested converter to that is used in conjunction with the adapter.
     */
    public CoreConverter<DatabaseMapping, Session> getNestedConverter() {
        return nestedConverter;
    }

    /**
     * Set a nested converter to be used in conjunction with the adapter. On marshal,
     * the nested converter is invoked after the adapter. On umarshal it is invoked before.
     * Primarily used to support enumerations with adapters.
     */
    public void setNestedConverter(CoreConverter<DatabaseMapping, Session> nestedConverter) {
        this.nestedConverter = nestedConverter;
    }
}
