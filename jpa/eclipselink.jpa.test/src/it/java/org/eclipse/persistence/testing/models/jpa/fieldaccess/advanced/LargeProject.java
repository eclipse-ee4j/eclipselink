/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import jakarta.persistence.*;

import org.eclipse.persistence.annotations.CascadeOnDelete;

/**
 * Local interface for the large project bean.
 * This is the bean's public/local interface for the clients usage.
 * All locals must extend the jakarta.ejb.EJBLocalObject.
 * The bean itself does not have to implement the local interface, but must implement all of the methods.
 */
@Entity(name="LargeProject")
@Table(name="CMP3_FA_LPROJECT")
@CascadeOnDelete
@DiscriminatorValue("L")
@NamedQueries({
@NamedQuery(
    name="findFieldAccessWithBudgetLargerThan",
    query="SELECT OBJECT(project) FROM LargeProject project WHERE project.budget >= :amount"
),
@NamedQuery(
    name="constructFieldAccessLProject",
    query="SELECT new org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced.LargeProject(project.name) FROM LargeProject project")
}
)
public class LargeProject extends Project {
    private double budget;

    public LargeProject () {
        super();
        fieldOnlySetThroughConstructor = 2;
    }

    public LargeProject (String name) {
        this();
        this.setName(name);
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
            this.budget = budget;
    }

    /**
     * This tests accessing a superclass field.
     */
    public Employee getTeamLeader() {
        return teamLeader;
    }
}
