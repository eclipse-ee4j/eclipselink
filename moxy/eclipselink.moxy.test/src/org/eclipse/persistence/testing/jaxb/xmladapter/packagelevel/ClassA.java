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
// Denise Smith - September 10 /2009
package org.eclipse.persistence.testing.jaxb.xmladapter.packagelevel;

import java.util.ArrayList;
import java.util.List;

public class ClassA {
    public String theValue;
    public List<String> theValues;

    public ClassA(){
        theValues = new ArrayList<String>();
    }

    public String getTheValue() {
        return theValue;
    }

    public void setTheValue(String theValue) {
        this.theValue = theValue;
    }

    public boolean equals(Object obj){
        if(!(obj instanceof ClassA)){
            return false;
        }
        ClassA classAObj = (ClassA)obj;

        if(theValue == null){
            if(classAObj.getTheValue() != null){
                return false;
            }
        }else{
            if(classAObj.getTheValue() == null){
                return false;
            }
            if(!getTheValue().equals(classAObj.getTheValue())){
                return false;
            }
        }

        return true;
    }
}
