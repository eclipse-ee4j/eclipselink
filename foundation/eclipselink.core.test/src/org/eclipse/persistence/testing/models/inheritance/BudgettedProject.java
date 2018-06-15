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
package org.eclipse.persistence.testing.models.inheritance;

import java.io.StringWriter;

/**
 * <b>Purpose</b>: Larger scale projects within the Employee Demo
 * <p><b>Description</b>:     LargeProject is a concrete subclass of Project. It is instantiated for Projects with type = 'L'. The additional
 *                                information (budget, & milestoneVersion) are mapped from the LPROJECT table.
 *    @see Project
 */
/**
 * Subclass of base project designed to test cyclical relationships with inheritance.
 */
public class BudgettedProject extends BaseProject {
    private String title;
    private Integer budget;

    public Integer getBudget() {
        return budget;
    }

    public String getTitle() {
        return title;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public void setTitle(String thing) {
        this.title = thing;
    }

    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("Large Project: ");
        writer.write(getName());
        writer.write(" ");
        writer.write(" " + getBudget());
        writer.write(" ");
        return writer.toString();
    }
}
