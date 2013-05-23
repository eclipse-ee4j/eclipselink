/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.6 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.internal.oxm;

import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.oxm.record.AbstractUnmarshalRecord;

public interface ConversionManager {

    public String buildBase64StringFromBytes(byte[] bytes);

    public QName buildQNameFromString(String stringValue, AbstractUnmarshalRecord record);

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
     * INTERNAL:
     * Converts a String which is in Base64 format to a Byte[]
     */
    public byte[] convertSchemaBase64ToByteArray(Object sourceObject);

}
