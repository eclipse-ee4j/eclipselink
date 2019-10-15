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
//     Denise Smith - EclipseLink 2.4
package org.eclipse.persistence.testing.jaxb.xmlidref.inheritance;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class School {
    @XmlElement(name="students/student")
    List<Student> students;

    @XmlIDREF
    Student classPresident;

    public School(){
        students = new ArrayList();
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Student getClassPresident() {
        return classPresident;
    }

    public void setClassPresident(Student classPresident) {
        this.classPresident = classPresident;
    }

    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj instanceof School){
            School theSchool = (School)obj;
            if(!classPresident.equals(theSchool.getClassPresident())){
                return false;
            }
            if(students.size() != theSchool.getStudents().size()){
                return false;
            }
            if(!(students.containsAll(theSchool.getStudents()))){
                return false;
            }
            return true;

        }
        return false;

    }

}
