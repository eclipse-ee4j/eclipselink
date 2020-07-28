/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.6 - initial implementation
package org.eclipse.persistence.internal.oxm;

import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.core.queries.CoreContainerPolicy;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.oxm.record.AbstractUnmarshalRecord;

public interface ConversionManager {

    public String buildBase64StringFromBytes(byte[] bytes);

    public QName buildQNameFromString(String stringValue, AbstractUnmarshalRecord record);

    /**
     * Removes all leading and trailing whitespaces, and replaces any sequences of whitespaces
     * that occur in the string with a single ' ' character.
     * @since EclipseLink 2.6.0
     */
    public String collapseStringValue(String value);

    /**
     * Convert the given object to the appropriate type by invoking the appropriate
     * ConversionManager method.
     *
     * @param sourceObject - will always be a string if read from XML
     * @param javaClass - the class that the object must be converted to
     * @param schemaTypeQName - the XML schema that the object is being converted from
     * @return - the newly converted object
     */
    public Object convertObject(Object sourceObject, Class javaClass, QName schemaTypeQName);

    /**
     * @since EclipseLink 2.6.0
     */
    public Object convertSchemaBase64ListToByteArrayList(Object sourceObject, CoreContainerPolicy containerPolicy, CoreAbstractSession session);

    public Object convertHexBinaryListToByteArrayList(Object sourceObject, CoreContainerPolicy containerPolicy, CoreAbstractSession session);
    /**
     * INTERNAL:
     * Converts a String which is in Base64 format to a Byte[]
     */
    public byte[] convertSchemaBase64ToByteArray(Object sourceObject);

    /**
     * @since EclipseLink 2.6.0
     * @param schemaType The type you want to find a corresponding Java class for.
     * @return the Java class for the XML schema type.
     */
    public Class<?> javaType(QName schemaType);

    /**
     * Replaces any CR, Tab or LF characters in the string with a single ' ' character.
     * @since EclipseLink 2.6.0
     */
    public String normalizeStringValue(String value);

    /**
     * @since EclipseLink 2.6.0
     * @param javaType The type you want to find a corresponding schema type for.
     * @return the schema type for the Java class.
     */
    public QName schemaType(Class<?> javaType);

}
