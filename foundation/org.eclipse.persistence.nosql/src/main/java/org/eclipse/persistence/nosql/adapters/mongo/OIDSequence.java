/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.nosql.adapters.mongo;

import java.util.Vector;

import org.bson.types.ObjectId;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;

public class OIDSequence extends Sequence {

    public OIDSequence() {
        super();
    }

    public OIDSequence(String name) {
        super(name);
    }

    @Override
    public Object getGeneratedValue(Accessor accessor, AbstractSession writeSession, String seqName) {
        return new ObjectId().toByteArray();
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
