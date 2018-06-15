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


import java.io.*;

/**
 * STI stands for Single Table Inheritance.
 * STI_Project references and referenced by STI_Employee class,
 * STI_Project is mapped with its subclasses STI_SmallProject and STI_LargeProject
 * to a single table.
 * STI_LargeProject is a concrete subclass of STI_Project. It is instantiated for STI_Projects with type = 'L'. The additional
 * information (budget) is mapped from the STI_PROJECT table.
 * @see STI_Project
 */
public class STI_LargeProject extends STI_Project {
    public double budget;

    public STI_LargeProject() {
        this.budget = 0.0;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    /**
     * Print the project's data.
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("STI_LargeProject: ");
        writer.write(getName());
        writer.write(" ");
        writer.write(getDescription());
        writer.write(" " + getBudget());
        return writer.toString();
    }
}
