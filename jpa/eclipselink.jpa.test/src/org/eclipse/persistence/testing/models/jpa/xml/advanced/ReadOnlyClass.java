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
//     Guy Pelletier (Oracle), February 28, 2007
//        - New test file introduced for bug 217880.
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

// This class is mapped as a read-only descriptor and used for testing.
public class ReadOnlyClass {
    public int id;

    public ReadOnlyClass() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
