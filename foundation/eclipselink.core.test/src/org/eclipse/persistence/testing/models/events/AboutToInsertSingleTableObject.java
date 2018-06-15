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
package org.eclipse.persistence.testing.models.events;

/**
 * CR#3237
 * Domain Object for AboutToInsert tests
 * This object maps one object to a single table
 */
public class AboutToInsertSingleTableObject {
    private java.lang.Double id;

    public java.lang.Double getId() {
        return id;
    }

    public void setId(java.lang.Double id) {
        this.id = id;
    }
}
