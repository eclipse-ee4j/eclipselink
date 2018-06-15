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
//     Denise Smith - EclipseLink 2.4
package org.eclipse.persistence.testing.jaxb.xmlidref.inheritance;

public class TransferStudent extends Student{

    private String previousSchool;

    public String getPreviousSchool() {
        return previousSchool;
    }

    public void setPreviousSchool(String previousSchool) {
        this.previousSchool = previousSchool;
    }

    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        boolean equals = super.equals(obj);
        if(!equals){
            return false;
        }
        if(obj instanceof TransferStudent){
            TransferStudent studentObject = (TransferStudent)obj;
            if(previousSchool == null && studentObject.getPreviousSchool()!= null){
                return false;
            }
            return previousSchool.equals(studentObject.getPreviousSchool());
        }
        return false;
    }

}
