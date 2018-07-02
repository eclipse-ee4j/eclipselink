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
package org.eclipse.persistence.testing.models.interfaces;

import org.eclipse.persistence.tools.schemaframework.*;

public class CourseDeveloper extends DevelopmentJob {
    public String course;

    public static TableDefinition courseDeveloperTable() {
        TableDefinition table = new TableDefinition();

        table.setName("CRS_DEV");
        table.addField("CODE", java.math.BigDecimal.class, 15);
        table.addField("COURSE", String.class, 60);

        return table;
    }

    public static CourseDeveloper example1() {
        CourseDeveloper example = new CourseDeveloper();

        example.setCourse("Intro to Quake 2 Map Design using Qoole");
        example.setSalary(new Float(52000.00));

        return example;
    }

    public static CourseDeveloper example2() {
        CourseDeveloper example = new CourseDeveloper();

        example.setCourse("Introduction to Synthesis");
        example.setSalary(new Float(51000.00));

        return example;
    }

    public static CourseDeveloper example3() {
        CourseDeveloper example = new CourseDeveloper();

        example.setCourse("Welcome to Ada!");
        example.setSalary(new Float(56000.00));

        return example;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String toString() {
        return new String("Course Developer: " + getJobCode());
    }
}
