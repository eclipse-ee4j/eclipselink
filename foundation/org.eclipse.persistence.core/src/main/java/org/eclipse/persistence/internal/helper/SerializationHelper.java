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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * <p>Provide common functionalities for serialization of object.
 * </p>
 *
 * <p>This class throws exceptions for invalid <code>null</code> inputs.
 * Each method documents its behavior in more detail.</p>
 *
 * @author Steven Vo
 * @since OracleAS 10.0.3
 */
public class SerializationHelper {

    /**
     * <p>Deep clone a Serializable object using serialization.
     * @param object the serializable object
     * @return the deep cloned object
     */
    public static Object clone(Serializable object) throws IOException, ClassNotFoundException {
        return deserialize(serialize(object));
    }

    /**
     * Serialize the object to an OutputStream
     *
     * @param obj  the object to serialize to bytes
     * @param outputStream  the stream to write to, can not be null
     */
    public static void serialize(Serializable obj, OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            throw new IllegalArgumentException("The outputStream argument cannot be null");
        }

        try (ObjectOutputStream outStream = new ObjectOutputStream(outputStream)) {
            // stream closed in the finally
            outStream.writeObject(obj);
        }
    }

    /**
     * Serialize the object to a byte array
     *
     * @param obj  the object to serialize to bytes
     * @return a byte[] of the obj
     */
    public static byte[] serialize(Serializable obj) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream(512);
        serialize(obj, outStream);
        return outStream.toByteArray();
    }

    /**
     * Deserialze an object from an InputStream
     *
     * @param inputStream  the serialized object input stream, must not be null
     * @return the deserialized object
     */
    public static Object deserialize(InputStream inputStream) throws IOException, ClassNotFoundException {
        if (inputStream == null) {
            throw new IllegalArgumentException("The inputStream argument cannot be null");
        }
        try (ObjectInputStream inStream = new ObjectInputStream(inputStream)) {
            // stream closed in the finally
            return inStream.readObject();
        }
    }

    /**
     * Deserialize an object from a byte array
     *
     * @param objectBytes  the serialized object, can not be null
     * @return the deserialized object
     */
    public static Object deserialize(byte[] objectBytes) throws IOException, ClassNotFoundException {
        if (objectBytes == null) {
            throw new IllegalArgumentException("The objectBytes argument cannot be null");
        }
        ByteArrayInputStream inStream = new ByteArrayInputStream(objectBytes);
        return deserialize(inStream);
    }
}
