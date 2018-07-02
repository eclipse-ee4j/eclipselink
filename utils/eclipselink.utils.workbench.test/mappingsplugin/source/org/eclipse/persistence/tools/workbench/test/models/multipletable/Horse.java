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
package org.eclipse.persistence.tools.workbench.test.models.multipletable;


/**
 * A horse object uses mutliple table primary key.
 *
 * @auther Guy Pelletier
 * @version 1.0
 * @date June 17, 2005
 */
public class Horse {
    protected int id;
    protected String name;
    protected int foalCount;

    public Horse() {
    }

    public int getFoalCount() {
        return this.foalCount;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setFoalCount(int foalCount) {
        this.foalCount = foalCount;
    }

    public void setName(String name) {
        this.name = name;
    }
}
