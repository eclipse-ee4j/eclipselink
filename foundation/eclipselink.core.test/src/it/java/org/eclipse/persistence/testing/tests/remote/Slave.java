/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.remote;

import java.io.*;

public class Slave implements Serializable // maps to a table SLAVE containing a foreign key to table MASTER
{

    private int primaryKey;
    private Master master; // every slave knows his master

    public Master getMaster() {
        return master;
    }

    public void setMaster(Master master) {
        this.master = master;
    }

    public void setId(int id) {
        primaryKey = id;
    }

    /**
     * Override the default serialization behavior to keep track of whether this object
     * has been serialized.
     */
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        if (master != null) {
            master.setSlavesSerialized(true);
        }
    }


    /**
     * Override the default serialization behavior to keep track of whether this object
     * has been serialized.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if (master != null) {
            master.setSlavesSerialized(true);
        }
    }

}
