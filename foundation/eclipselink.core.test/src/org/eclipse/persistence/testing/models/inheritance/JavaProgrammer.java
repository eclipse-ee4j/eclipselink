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
package org.eclipse.persistence.testing.models.inheritance;

import org.eclipse.persistence.testing.models.inheritance.Programmer;

public class JavaProgrammer extends Programmer {
    private int numberOfSupportQuestions;

    public JavaProgrammer() {
        super();
    }

    public static JavaProgrammer example1() {
        JavaProgrammer J1 = new JavaProgrammer();
        J1.setNumberOfSupportQuestions(5);
        J1.setSalary(35000);
        J1.setName("Rick Giles");
        J1.setGender("Male");
        J1.setWeight(120);
        J1.setSize("Medium");
        return J1;
    }

    public static JavaProgrammer example2() {
        JavaProgrammer J1 = new JavaProgrammer();
        J1.setNumberOfSupportQuestions(2);
        J1.setSalary(100000);
        J1.setName("Someone");
        J1.setGender("Female");
        J1.setWeight(120);
        J1.setSize("small");
        return J1;
    }

    public static JavaProgrammer steve() {
        JavaProgrammer J1 = new JavaProgrammer();
        J1.setNumberOfSupportQuestions(400);
        J1.setSalary(20);
        J1.setName("Steve");
        J1.setGender("Male");
        J1.setWeight(140);
        J1.setSize("medium");
        return J1;
    }

    public int getNumberOfSupportQuestions() {
        return numberOfSupportQuestions;
    }

    public void setNumberOfSupportQuestions(int num) {
        numberOfSupportQuestions = num;
    }
}
