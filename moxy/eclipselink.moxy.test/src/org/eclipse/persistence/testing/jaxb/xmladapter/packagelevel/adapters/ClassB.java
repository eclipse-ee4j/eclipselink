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
package org.eclipse.persistence.testing.jaxb.xmladapter.packagelevel.adapters;

public class ClassB {
    public String someValue;

    public String getSomeValue() {
        return someValue;
    }

    public void setSomeValue(String someValue) {
        this.someValue = someValue;
    }

    public boolean equals(Object obj){
        if(!(obj instanceof ClassB)){
            return false;
        }
        ClassB classBObj = (ClassB)obj;

        if(someValue == null){
            if(classBObj.getSomeValue() != null){
                return false;
            }
        }else{
            if(classBObj.getSomeValue() == null){
                return false;
            }
            if(!someValue.equals(classBObj.getSomeValue())){
                return false;
            }
        }

        return true;
    }
}
