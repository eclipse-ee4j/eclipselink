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
package org.eclipse.persistence.testing.oxm.mappings.namespaces;

public class Project {
    String name;
    String description;

    public Project() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

     public boolean equals(Object o) {
        try {
            Project project = (Project)o;
            if (!this.getName().equals(project.getName())) {
                return false;
            }
            if (!this.getDescription().equals(project.getDescription())) {
                return false;
            }
        } catch (ClassCastException e) {
            return false;
        }
        return true;
     }
}
