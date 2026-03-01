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
package org.eclipse.persistence.testing.tests.optimisticlocking;

import org.eclipse.persistence.internal.sessions.ObjectChangeSet;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Bug 3526981
 * Ensure the hasVersionChange variable is not always set when ObjectChangeSet is serialized.
 * This would happen as a consequence of calling setWriteLockValue on the ObjectChangeSet during serialization.
 */
public class WriteLockValueSerializationTest extends AutoVerifyTestCase {
    public boolean serializationError = true;

    public WriteLockValueSerializationTest() {
        setDescription("Ensure the hasVersionChange variable is not always set when ObjectChangeSet is serialized.");
    }

    @Override
    public void setup() {
        serializationError = false;
    }

    @Override
    public void test() {
        ObjectChangeSet changeSet = new ObjectChangeSet();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(changeSet);
            oos.flush();
            byte[] bytes = baos.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            changeSet = (ObjectChangeSet)ois.readObject();
        } catch (java.io.IOException exception) {
            throw new TestErrorException("The change set did not serialize properly. IOException:" + exception);
        } catch (ClassNotFoundException exception) {
            throw new TestErrorException("The change set did not serialize properly. ClassNotFoundException:" + exception);
        }
        if (changeSet.hasChanges()) {
            serializationError = true;
        }
    }

    @Override
    public void verify() {
        if (serializationError) {
            throw new TestErrorException("Object Change set returns true for hasChanges after serialization even though there are not changes.");
        }
    }
}
