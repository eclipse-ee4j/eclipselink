/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//      Oracle - initial impl
package org.eclipse.persistence.sessions.serializers;

import org.eclipse.persistence.internal.helper.CustomObjectInputStream;
import org.eclipse.persistence.internal.security.EclipseLinkSerialFilter;
import org.eclipse.persistence.sessions.Session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Plain old Java serialization.
 * @author James Sutherland
 */
public class JavaSerializer extends AbstractSerializer {

    public static final JavaSerializer instance = new JavaSerializer();

    @Override
    public Object serialize(Object object, Session session) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOut = new ObjectOutputStream(byteOut);
            objectOut.writeObject(object);
            objectOut.flush();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return byteOut.toByteArray();
    }

    @Override
    public Class<?> getType() {
        return byte[].class;
    }

    @Override
    public Object deserialize(Object bytes, Session session) {
        return deserialize(bytes, session, null);
    }

    /**
     * Deserialize bytes from a remote command or remote session.
     * Java serialization is filtered. Other serializers are unchanged.
     */
    public static Object deserializeRemote(Serializer serializer, Object bytes, Session session) {
        if (serializer instanceof JavaSerializer javaSerializer) {
            return javaSerializer.deserialize(bytes, session, EclipseLinkSerialFilter.create(session));
        }
        return serializer.deserialize(bytes, session);
    }

    /**
     * @param filter {@code null} keeps the historical unfiltered read used by column converters
     */
    public Object deserialize(Object bytes, Session session, ObjectInputFilter filter) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream((byte[])bytes);
        try (ObjectInputStream objectIn = session == null
                ? new ObjectInputStream(byteIn)
                : new CustomObjectInputStream(byteIn, session)) {
            if (filter != null) {
                objectIn.setObjectInputFilter(filter);
            }
            return objectIn.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
