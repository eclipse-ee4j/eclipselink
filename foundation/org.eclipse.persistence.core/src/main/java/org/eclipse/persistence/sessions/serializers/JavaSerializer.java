/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//      Oracle - initial impl
package org.eclipse.persistence.sessions.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.persistence.internal.helper.CustomObjectInputStream;
import org.eclipse.persistence.sessions.Session;

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
    public Class getType() {
        return byte[].class;
    }

    @Override
    public Object deserialize(Object bytes, Session session) {
        ByteArrayInputStream byteIn = new ByteArrayInputStream((byte[])bytes);
        try (ObjectInputStream objectIn = session == null
                ? new ObjectInputStream(byteIn)
                : new CustomObjectInputStream(byteIn, session)) {
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
