/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     03/04/09 tware - test for bug 350599 copied from advanced model
package org.eclipse.persistence.testing.models.jpa.privateowned;

public class MountPK {

    public int id;
    public int id2;

    public MountPK(int id, int id2){
        this.id = id;
        this.id2 = id;
    }
}
