/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.multipletable;

/**
 * A Human object uses multiple table primary key.
 *
 * @author Guy Pelletier
 * @version 1.0
 */
public class Human {
    protected int id;
    protected String name;
    protected int kidCount;

    public Human() {
    }

    public int getKidCount() {
        return this.kidCount;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setKidCount(int kidCount) {
        this.kidCount = kidCount;
    }

    public void setName(String name) {
        this.name = name;
    }
}
