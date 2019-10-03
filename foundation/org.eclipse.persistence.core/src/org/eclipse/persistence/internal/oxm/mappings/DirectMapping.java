/*
 * Copyright (c) 2012, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5 - initial implementation
package org.eclipse.persistence.internal.oxm.mappings;

import org.eclipse.persistence.core.descriptors.CoreDescriptor;
import org.eclipse.persistence.core.mappings.CoreAttributeAccessor;
import org.eclipse.persistence.core.mappings.converters.CoreConverter;
import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.internal.core.helper.CoreField;
import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.Marshaller;
import org.eclipse.persistence.internal.oxm.Unmarshaller;
import org.eclipse.persistence.internal.oxm.record.AbstractMarshalRecord;
import org.eclipse.persistence.internal.oxm.record.AbstractUnmarshalRecord;
import org.eclipse.persistence.internal.oxm.record.XMLRecord;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;

public interface DirectMapping<
    ABSTRACT_SESSION extends CoreAbstractSession,
    ATTRIBUTE_ACCESSOR extends CoreAttributeAccessor,
    CONTAINER_POLICY extends CoreContainerPolicy,
    CONVERTER extends CoreConverter,
    DESCRIPTOR extends CoreDescriptor,
    FIELD extends CoreField,
    MARSHALLER extends Marshaller,
    SESSION extends CoreSession,
    UNMARSHALLER extends Unmarshaller,
    XML_RECORD extends XMLRecord> extends Mapping<ABSTRACT_SESSION, ATTRIBUTE_ACCESSOR, CONTAINER_POLICY, DESCRIPTOR, FIELD, XML_RECORD>, XMLConverterMapping<MARSHALLER, SESSION, UNMARSHALLER> {

    Object getAttributeValue(Object object, ABSTRACT_SESSION session, AbstractUnmarshalRecord record);

    /**
     * Return the converter on the mapping.
     * A converter can be used to convert between the object's value and database value of the attribute.
     */
    CONVERTER getConverter();

    Object getFieldValue(Object object, CoreAbstractSession session, AbstractMarshalRecord record);

    AbstractNullPolicy getNullPolicy();

    Object getNullValue();

    Object getObjectValue(Object object, SESSION session);

    /**
     * Get the XPath String
     * @return String the XPath String associated with this Mapping
     */
    String getXPath();

    boolean hasConverter();

    boolean isCDATA();

    /**
     * Some databases do not properly support all of the base data types. For these databases,
     * the base data type must be explicitly specified in the mapping to tell EclipseLink to force
     * the instance variable value to that data type
     */
    void setAttributeClassification(Class attributeClassification);


    /**
     * INTERNAL:
     * Set the name of the class for MW usage.
     */
    void setAttributeClassificationName(String attributeClassificationName);

    /**
     * Indicates that this mapping should collapse all string values before setting them
     * in the object on unmarshal. Collapse removes leading and trailing whitespaces, and replaces
     * any sequence of whitepsace characters with a single space.
     * @param collapse
     */
    void setCollapsingStringValues(boolean collapse);

    /**
     * Set the converter on the mapping.
     * A converter can be used to convert between the object's value and database value of the attribute.
     */
    void setConverter(CONVERTER converter);

    /**
     * ADVANCED:
     * Set the field in the mapping.
     * This can be used for advanced field types, such as XML nodes, or to set the field type.
     */
    void setField(FIELD theField);


    void setIsCDATA(boolean CDATA);

    void setIsWriteOnly(boolean b);

    /**
     * Indicates that this mapping should normalize all string values before setting them
     * in the object on unmarshal. Normalize replaces any CR, LF or Tab characters with a
     * single space character.
     * @param normalize
     */
    void setNormalizingStringValues(boolean normalize);

    /**
     * Set the AbstractNullPolicy on the mapping<br>
     * The default policy is NullPolicy.<br>
     *
     * @param aNullPolicy
     */
    void setNullPolicy(AbstractNullPolicy aNullPolicy);

    /**
     * Allow for the value used for null to be specified.
     * This can be used to convert database null values to application specific values, when null values
     * are not allowed by the application (such as in primitives).
     * Note: the default value for NULL is used on reads, writes, and query SQL generation
     */
    void setNullValue(Object nullValue);

    /**
     * Set whether this mapping's value should be marshalled, in the case that
     * it is equal to the default null value.
     */
    void setNullValueMarshalled(boolean value);


    /**
     * Set the Mapping field name attribute to the given XPath String
     * @param xpathString String
     */
    void setXPath(String xpathString);

    Object valueFromObject(Object object, FIELD field, ABSTRACT_SESSION abstractSession);

}
