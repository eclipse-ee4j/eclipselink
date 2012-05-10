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
import org.eclipse.persistence.expressions.*;

public class Student {
    public Course course;
    public int st_ID;

    /**
     * Student constructor comment.
     */
    public Student() {
        setCourse(new Course());
        setSt_ID(0);

    }

    public static void addToDescriptor(org.eclipse.persistence.descriptors.ClassDescriptor descrip) {
        descrip.addForeignKeyFieldNameForMultipleTable("STUDENT.C_ID", "STUDENT2.COURSE_ID");
        ExpressionBuilder b = new ExpressionBuilder();
        descrip.getQueryManager().setMultipleTableJoinExpression(b.getField("STUDENT2.COURSE_ID").equalOuterJoin(b.getField("STUDENT.C_ID")));
    }

    public static Student example1() {
        Student example = new Student();

        example.setSt_ID(1001);
        example.setCourse(Course.example1());
        return example;
    }

    public static Student example10() {
        Student example = new Student();
        example.setSt_ID(1010);
        example.setCourse(Course.example10());
        return example;
    }

    public static Student example11() {
        Student example = new Student();
        example.setSt_ID(1011);
        example.setCourse(Course.example11());
        return example;
    }

    public static Student example12() {
        Student example = new Student();
        example.setSt_ID(1012);
        example.setCourse(Course.example12());
        return example;
    }

    public static Student example13() {
        Student example = new Student();
        example.setSt_ID(1013);
        example.setCourse(Course.example13());
        return example;
    }

    public static Student example14() {
        Student example = new Student();
        example.setSt_ID(1014);
        example.setCourse(Course.example14());
        return example;
    }

    public static Student example15() {
        Student example = new Student();
        example.setSt_ID(1015);
        example.setCourse(Course.example15());
        return example;
    }

    public static Student example2() {
        Student example = new Student();
        example.setSt_ID(1002);
        example.setCourse(Course.example2());
        return example;
    }

    public static Student example3() {
        Student example = new Student();
        example.setSt_ID(1003);
        example.setCourse(Course.example3());
        return example;
    }

    public static Student example4() {
        Student example = new Student();
        example.setSt_ID(1004);
        example.setCourse(Course.example4());
        return example;
    }

    public static Student example5() {
        Student example = new Student();
        example.setSt_ID(1005);
        example.setCourse(Course.example5());
        return example;
    }

    public static Student example6() {
        Student example = new Student();
        example.setSt_ID(1006);
        example.setCourse(Course.example6());
        return example;
    }

    public static Student example7() {
        Student example = new Student();
        example.setSt_ID(1007);
        example.setCourse(Course.example7());
        return example;
    }

    public static Student example8() {
        Student example = new Student();
        example.setSt_ID(1008);
        example.setCourse(Course.example8());
        return example;
    }

    public static Student example9() {
        Student example = new Student();
        example.setSt_ID(1009);
        example.setCourse(Course.example9());
        return example;
    }

    public Course getCourse() {
        return course;
    }

    public int getSt_ID() {
        return st_ID;
    }

    public void setCourse(Course newCourse) {
        this.course = newCourse;
    }

    public void setSt_ID(int newValue) {
        this.st_ID = newValue;
    }

    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("STUDENT");

        definition.addPrimaryKeyField("ST_ID", java.math.BigDecimal.class, 15);
        definition.addField("COURSE", String.class, 40);
        definition.addField("C_ID", java.math.BigDecimal.class, 15);

        return definition;
    }
}
