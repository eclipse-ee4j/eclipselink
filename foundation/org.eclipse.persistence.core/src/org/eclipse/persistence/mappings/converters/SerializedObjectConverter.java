/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.mappings.converters;

import java.security.AccessController;

import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.descriptors.ClassNameConversionRequired;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.sessions.serializers.JSONSerializer;
import org.eclipse.persistence.sessions.serializers.JavaSerializer;
import org.eclipse.persistence.sessions.serializers.Serializer;
import org.eclipse.persistence.sessions.serializers.XMLSerializer;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>: The serialized object converter can be used to store an arbitrary object or set of objects into a database binary or character field.
 * By default it uses the Java serializer so the target must be serializable.
 * A custom Serializer can also be specified, such as XML or JSON.
 *
 * @see Serializer
 * @see XMLSerializer
 * @see JSONSerializer
 * @author James Sutherland
 * @since OracleAS TopLink 10<i>g</i> (10.0.3)
 */
public class SerializedObjectConverter implements Converter, ClassNameConversionRequired {
    protected DatabaseMapping mapping;
    protected Serializer serializer;
    protected String serializerClassName;
    protected String serializerPackage;

    /**
     * PUBLIC:
     * Default constructor.
     */
    public SerializedObjectConverter() {
        this.serializer = new JavaSerializer();
    }

    /**
     * PUBLIC:
     * Default constructor.
     */
    public SerializedObjectConverter(DatabaseMapping mapping) {
        this.mapping = mapping;
        this.serializer = new JavaSerializer();
    }

    /**
     * PUBLIC:
     * Default constructor.
     */
    public SerializedObjectConverter(DatabaseMapping mapping, Serializer serializer) {
        this.mapping = mapping;
        this.serializer = serializer;
    }

    /**
     * PUBLIC:
     * Default constructor.
     */
    public SerializedObjectConverter(DatabaseMapping mapping, String serializerClassName) {
        this.mapping = mapping;
        this.serializerClassName = serializerClassName;
    }

    /**
     * INTERNAL:
     * Convert all the class-name-based settings in this converter to actual class-based
     * settings. This method is used when converting a project that has been built
     * with class names to a project with classes.
     * This method is implemented by subclasses as necessary.
     * @param classLoader
     */
    public void convertClassNamesToClasses(ClassLoader classLoader) {
        try{
            if (this.serializerClassName != null) {
                Class serializerClass = null;
                if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                    serializerClass = AccessController.doPrivileged(new PrivilegedClassForName(this.serializerClassName, true, classLoader));
                } else {
                    serializerClass = PrivilegedAccessHelper.getClassForName(this.serializerClassName, true, classLoader);
                }
                this.serializer = (Serializer)serializerClass.newInstance();
            }
        } catch (Exception exception){
            throw ValidationException.classNotFoundWhileConvertingClassNames(this.serializerClassName, exception);
        }
    }

    /**
     * INTERNAL:
     * The fieldValue will be a byte array.  Create a ByteArrayInputStream
     * on the fieldValue.  Create an ObjectInputStream on the ByteArrayInputStream
     * to read in the objects.
     */
    public Object convertDataValueToObjectValue(Object fieldValue, Session session) throws DescriptorException {
        if (fieldValue == null) {
            return null;
        }
        Object data = fieldValue;
        if (this.serializer.getType() == ClassConstants.APBYTE) {
            byte[] bytes;
            try {
                bytes = (byte[])((AbstractSession)session).getDatasourcePlatform().convertObject(fieldValue, ClassConstants.APBYTE);
            } catch (ConversionException exception) {
                throw ConversionException.couldNotBeConverted(this.mapping, this.mapping.getDescriptor(), exception);
            }
            if ((bytes == null) || (bytes.length == 0)) {
                return null;
            }
            data = bytes;
        } else if (this.serializer.getType() == ClassConstants.STRING) {
            String text;
            try {
                text = (String)((AbstractSession)session).getDatasourcePlatform().convertObject(fieldValue, ClassConstants.STRING);
            } catch (ConversionException exception) {
                throw ConversionException.couldNotBeConverted(this.mapping, this.mapping.getDescriptor(), exception);
            }
            if ((text == null) || (text.length() == 0)) {
                return null;
            }
            data = text;
        }
        try {
            return this.serializer.deserialize(data, session);
        } catch (Exception exception) {
            throw DescriptorException.notDeserializable(getMapping(), exception);
        }
    }

    /**
     *  INTERNAL:
     *  Convert the object to a byte array through serialize.
     */
    public Object convertObjectValueToDataValue(Object attributeValue, Session session) {
        if (attributeValue == null) {
            return null;
        }
        try {
            return this.serializer.serialize(attributeValue, session);
        } catch (Exception exception) {
            throw DescriptorException.notSerializable(getMapping(), exception);
        }
    }

    /**
     * INTERNAL:
     * Set the mapping.
     */
    public void initialize(DatabaseMapping mapping, Session session) {
        this.mapping = mapping;
        // CR#... Mapping must also have the field classification.
        if (getMapping().isDirectToFieldMapping()) {
            AbstractDirectMapping directMapping = (AbstractDirectMapping)getMapping();

            // Allow user to specify field type to override computed value. (i.e. blob, nchar)
            if (directMapping.getFieldClassification() == null) {
                directMapping.setFieldClassification(getSerializer().getType());
            }
        }

        if (this.serializer != null) {
            this.serializer.initialize(mapping.getAttributeClassification(), this.serializerPackage, session);
        }
    }

    /**
     * INTERNAL:
     * Return the mapping.
     */
    protected DatabaseMapping getMapping() {
        return mapping;
    }

    /**
     * INTERNAL:
     * If the converter converts the value to a non-atomic value, i.e.
     * a value that can have its' parts changed without being replaced,
     * then it must return false, serialization can be non-atomic.
     */
    public boolean isMutable() {
        return true;
    }

    /**
     * Return the serialize used for this converter.
     */
    public Serializer getSerializer() {
        return serializer;
    }

    /**
     * Set the serialize used for this converter.
     */
    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    /**
     * Return the class name of the serializer.
     */
    public String getSerializerClassName() {
        return serializerClassName;
    }

    /**
     * Set the class name of the serializer.
     */
    public void setSerializerClassName(String serializerClassName) {
        this.serializerClassName = serializerClassName;
    }

    /**
     * Return the package used for XML and JSON serialization JAXBContext.
     */
    public String getSerializerPackage() {
        return serializerPackage;
    }

    /**
     * Set the package used for XML and JSON serialization JAXBContext.
     */
    public void setSerializerPackage(String serializerPackage) {
        this.serializerPackage = serializerPackage;
    }
}
