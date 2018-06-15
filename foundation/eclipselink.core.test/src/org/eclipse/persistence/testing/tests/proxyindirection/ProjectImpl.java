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

    public String getDescription() {
        return this.description;
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public void setID(int value) {
        this.id = value;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String toString() {
        return "[Project #" + getID() + "] " + getName() + " - " + getDescription();
    }
}
