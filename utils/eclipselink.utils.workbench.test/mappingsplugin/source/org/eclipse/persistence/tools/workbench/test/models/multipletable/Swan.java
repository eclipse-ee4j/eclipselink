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
 * A Swan object uses mutliple table foreign key.
 *
 * @auther Guy Pelletier
 * @version 1.0
 * @date May 28, 2007
 */
public class Swan {
    protected int id;
    protected String name;
    protected int cygnetCount;

    public Swan() {
    }

    public int getCygnetCount() {
        return this.cygnetCount;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setCygnetCount(int cygnetCount) {
        this.cygnetCount = cygnetCount;
    }

    public void setName(String name) {
        this.name = name;
    }
}
