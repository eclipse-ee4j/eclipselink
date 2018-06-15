/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.optimisticlocking;

public abstract class MusicalInstrument implements SelfUpdatable {
    public int id;
    public String colour;
    public String make;
    public String lockField;

    /**
     * Instrument constructor comment.
     */
    public MusicalInstrument() {
        super();
        updateLockField();
    }

    public void updateLockField() {
        if (lockField == null) {
            lockField = "";
        }

        lockField = lockField + "#";

    }

    public void verify() {
    }
}
