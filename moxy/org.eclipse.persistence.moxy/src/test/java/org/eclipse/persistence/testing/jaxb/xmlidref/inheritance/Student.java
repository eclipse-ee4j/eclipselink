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

public class Student extends Person {

    private String studentNumber;

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        boolean equals = super.equals(obj);
        if(!equals){
            return false;
        }
        if(obj instanceof Student){
            Student studentObject = (Student)obj;
            if(studentNumber == null && studentObject.getStudentNumber()!= null){
                return false;
            }
            return studentNumber.equals(studentObject.getStudentNumber());
        }
        return false;
    }

}
