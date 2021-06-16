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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.remote;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.indirection.*;

public class Master implements Serializable// maps to a table MASTER
{

    private ValueHolderInterface slaves; // use basic indirection for one-to-many mapping
    private int primaryKey;
        private boolean slavesSerialized = false; // unmapped variable added to keep track or serialization for bug 3145211

    public Master(){
       slaves = new ValueHolder(new Vector());
    }

    public int getId(){
        return primaryKey;
    }

    public void setId(int id){
        this.primaryKey = id;
    }

    public void addSlave(Slave slave){
        getSlaves().addElement(slave);
                slave.setMaster(this);
    }

    public Vector getSlaves() {
        return (Vector)slaves.getValue();
    }

        public void setSlavesSerialized(boolean wasSerialized){
            this.slavesSerialized = wasSerialized;
        }

        public boolean slavesSerialized(){
            return slavesSerialized;
        }


}
