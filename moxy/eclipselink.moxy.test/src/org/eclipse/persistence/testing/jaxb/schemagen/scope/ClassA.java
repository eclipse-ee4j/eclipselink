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
// Denise Smith - September 15 /2009
package org.eclipse.persistence.testing.jaxb.schemagen.scope;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="classARoot")
public class ClassA {
    private String someValue;

    public String getSomeValue() {
        return someValue;
    }

    public void setSomeValue(String someValue) {
        this.someValue = someValue;
    }

    public boolean equals(Object obj){
        if(!(obj instanceof ClassA)){
            return false;
        }
        ClassA classAObj = (ClassA)obj;

        if(someValue == null){
            if(classAObj.getSomeValue() != null){
                return false;
            }
        }else{
            if(classAObj.getSomeValue() == null){
                return false;
            }
            if(!someValue.equals(classAObj.getSomeValue())){
                return false;
            }
        }

        return true;
    }
}
