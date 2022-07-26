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
package org.eclipse.persistence.testing.tests.proxyindirection;


/*
 * Project implementation.
 *
 * Implementation of the Cubicle interface.
 *
 * @author        Rick Barkhouse
 * @since        08/23/2000 16:37:59
 */
public class ProjectImpl implements Project {
    public int id;
    public String name;
    public String description;

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setDescription(String value) {
        this.description = value;
    }

    @Override
    public void setID(int value) {
        this.id = value;
    }

    @Override
    public void setName(String value) {
        this.name = value;
    }

    public String toString() {
        return "[Project #" + getID() + "] " + getName() + " - " + getDescription();
    }
}
