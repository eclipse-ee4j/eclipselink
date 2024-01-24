/*
 * Copyright (c) 2011, 2024 Oracle, IBM and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
//     08/10/2022-4.0 Jody Grassel
//        - ECL1535 : UUIDGenerator intermittently fails to initialize
package org.eclipse.persistence.sequencing;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.ValueReadQuery;

import java.util.UUID;
import java.util.Vector;

public class UUIDSequence extends Sequence {

    public UUIDSequence() {
        super();
    }

    public UUIDSequence(String name) {
        super(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UUIDSequence other) {
            return this.getName().equals(other.getName());
        } else {
            return false;
        }
    }

    @Override
    public Object getGeneratedValue(Accessor accessor, AbstractSession writeSession, String seqName) {
        ValueReadQuery query = null;
        if (getDatasourcePlatform() != null) {
            query = getDatasourcePlatform().getUUIDQuery();
        }
        if (query != null) {
            return writeSession.executeQuery(query);
        } else {
            return UUID.randomUUID().toString();
        }
    }

    @Override
    public Vector<?> getGeneratedVector(Accessor accessor, AbstractSession writeSession, String seqName, int size) {
        return null;
    }

    @Override
    public void onConnect() {
    }

    @Override
    public void onDisconnect() {
    }

    @Override
    public boolean shouldAcquireValueAfterInsert() {
        return false;
    }

    @Override
    public boolean shouldUseTransaction() {
        return false;
    }

    @Override
    public boolean shouldUsePreallocation() {
        return false;
    }

}
