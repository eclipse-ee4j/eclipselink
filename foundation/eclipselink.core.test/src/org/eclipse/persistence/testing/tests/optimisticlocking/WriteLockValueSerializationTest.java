/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.optimisticlocking;

import java.io.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.internal.sessions.*;

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

    public void setup() {
        serializationError = false;
    }

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
            throw new TestErrorException("The change set did not serialize properly. IOException:" + exception.toString());
        } catch (ClassNotFoundException exception) {
            throw new TestErrorException("The change set did not serialize properly. ClassNotFoundException:" + exception.toString());
        }
        if (changeSet.hasChanges()) {
            serializationError = true;
        }
    }

    public void verify() {
        if (serializationError) {
            throw new TestErrorException("Object Change set returns true for hasChanges after serialization even though there are not changes.");
        }
    }
}
