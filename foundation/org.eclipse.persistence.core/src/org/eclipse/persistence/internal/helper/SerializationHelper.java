/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.helper;

import org.eclipse.persistence.exceptions.ValidationException;
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
     * @param the serializable object
     * @return the deep cloned object
     * @throws  IOException, ClassNotFoundException
     */
    public static Object clone(Serializable object) throws IOException, ClassNotFoundException {
        return deserialize(serialize(object));
    }

    /**
     * Serialize the object to an OutputStream
     *
     * @param obj  the object to serialize to bytes
     * @param outputStream  the stream to write to, can not be null
     * @throws IOException
     */
    public static void serialize(Serializable obj, OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            throw ValidationException.invalidNullMethodArguments();
        }
        ObjectOutputStream outStream = null;

        try {
            // stream closed in the finally
            outStream = new ObjectOutputStream(outputStream);
            outStream.writeObject(obj);
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException ex) {
                // ignore;
            }
        }
    }

    /**
     * Serialize the object to a byte array
     *
     * @param obj  the object to serialize to bytes
     * @return a byte[] of the obj
     * @throws IOException
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
     * @throws IOException, ClassNotFoundException
     */
    public static Object deserialize(InputStream inputStream) throws IOException, ClassNotFoundException {
        if (inputStream == null) {
            throw new IllegalArgumentException("The inputStream argument cannot be null");
        }
        ObjectInputStream inStream = null;
        try {
            // stream closed in the finally
            inStream = new ObjectInputStream(inputStream);
            return inStream.readObject();

        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException ex) {
                // ignore
            }
        }
    }

    /**
     * Deserialize an object from a byte array
     *
     * @param objectBytes  the serialized object, can not be null
     * @return the deserialized object
     * @throws  IOException, ClassNotFoundException
     */
    public static Object deserialize(byte[] objectBytes) throws IOException, ClassNotFoundException {
        if (objectBytes == null) {
            throw ValidationException.invalidNullMethodArguments();
        }
        ByteArrayInputStream inStream = new ByteArrayInputStream(objectBytes);
        return deserialize(inStream);
    }
}
