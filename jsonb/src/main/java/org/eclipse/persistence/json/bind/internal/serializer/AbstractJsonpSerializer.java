/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.persistence.json.bind.internal.serializer;

import javax.json.stream.JsonGenerator;

/**
 * Common serializer functionality.
 *
 * @author Roman Grigoriadi
 */
public abstract class AbstractJsonpSerializer<T> {


    /**
     * Writes a value without json key.
     *
     * @param value value to write
     * @param jsonGenerator jsonGenerator to use
     */
    abstract void writeValue(T value, JsonGenerator jsonGenerator);

    /**
     * Write json key with value
     *
     * @param keyName key name
     * @param value value to write
     * @param jsonGenerator jsonGenerator to use
     */
    abstract void writeValue(String keyName, T value, JsonGenerator jsonGenerator);

    /**
     * True if serializer can serialize provided value.
     * @param value value to check
     * @param <X> Type of value
     * @throws NullPointerException if value is null
     * @return true if supports
     */
    abstract <X> boolean supports(X value);
}
