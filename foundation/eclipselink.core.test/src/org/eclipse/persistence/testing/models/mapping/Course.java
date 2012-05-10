/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.mapping;

import org.eclipse.persistence.tools.schemaframework.*;

public class Course {
    public int course_ID = 0000;
    public String courseName = "Math";
    public String prof = "Einstein";

    public Course() {
        setCourse_ID(0000);
        setCourseName("");
        setProf("hamster man");

    }

    public static Course example1() {
        Course example = new Course();

        example.setCourse_ID(1);
        example.setCourseName("Child Studies");
        example.setProf("Bill Gates");
        return example;
    }

    public static Course example10() {
        Course example = new Course();

        example.setCourse_ID(10);
        example.setCourseName("Hard Life Facts");
        example.setProf("Peter Pan");
        return example;
    }

    public static Course example11() {
        Course example = new Course();

        example.setCourse_ID(11);
        example.setCourseName("The Making of Star Wars");
        example.setProf("Captain Crunch");
        return example;
    }

    public static Course example12() {
        Course example = new Course();

        example.setCourse_ID(12);
        example.setCourseName("Hard Drive Excorcism");
        example.setProf("The Wack Minister on the Web");
        return example;
    }

    public static Course example13() {
        Course example = new Course();

        example.setCourse_ID(13);
        example.setCourseName("Downtown Traffice");
        example.setProf("OC Transpo Consultant");
        return example;
    }

    public static Course example14() {
        Course example = new Course();

        example.setCourse_ID(14);
        example.setCourseName("How to be Loser");
        example.setProf("Loser Boozer");
        return example;
    }

    public static Course example15() {
        Course example = new Course();

        example.setCourse_ID(15);
        example.setCourseName("Half Life Survival");
        example.setProf("Ricky Martin");
        return example;
    }

    public static Course example2() {
        Course example = new Course();

        example.setCourse_ID(2);
        example.setCourseName("Greek Culture");
        example.setProf("Bill Clinton");
        return example;
    }

    public static Course example3() {
        Course example = new Course();

        example.setCourse_ID(3);
        example.setCourseName("Soap Opera");
        example.setProf("Jean Chretien");
        return example;
    }

    public static Course example4() {
        Course example = new Course();

        example.setCourse_ID(4);
        example.setCourseName("Casino Tricks");
        example.setProf("Gary Chen");
        return example;
    }

    public static Course example5() {
        Course example = new Course();

        example.setCourse_ID(5);
        example.setCourseName("Politricks");
        example.setProf("Alberto Einstein");
        return example;
    }

    public static Course example6() {
        Course example = new Course();

        example.setCourse_ID(6);
        example.setCourseName("Pillow Fighting");
        example.setProf("Julie Champs");
        return example;
    }

    public static Course example7() {
        Course example = new Course();

        example.setCourse_ID(7);
        example.setCourseName("BlackJack Amateurs");
        example.setProf("Boris Yeltsin");
        return example;
    }

    public static Course example8() {
        Course example = new Course();

        example.setCourse_ID(8);
        example.setCourseName("Gee me a Break");
        example.setProf("Louis Pantalon");
        return example;
    }

    public static Course example9() {
        Course example = new Course();

        example.setCourse_ID((9));
        example.setCourseName("How to get a Life");
        example.setProf("The Colonel");
        return example;
    }

    /**
     The gets the course ID
     */
    public int getCourse_ID() {
        return course_ID;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getProf() {
        return prof;
    }

    /**
    This sets the course ID
     */
    public void setCourse_ID(int newValue) {
        this.course_ID = newValue;
    }

    public void setCourseName(String newValue) {
        this.courseName = newValue;
    }

    public void setProf(String newValue) {
        this.prof = newValue;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("STUDENT2");

        definition.addPrimaryKeyField("COURSE_ID", java.math.BigDecimal.class, 15);
        definition.addField("PROF", String.class, 40);
        definition.addField("COURSENAME", String.class, 40);

        return definition;
    }
}
