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
//     Denise Smith - EclipseLink 2.4
package org.eclipse.persistence.testing.jaxb.xmlidref.inheritance;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlRootElement;

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
