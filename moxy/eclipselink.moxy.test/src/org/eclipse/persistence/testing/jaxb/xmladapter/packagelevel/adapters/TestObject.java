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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="testObject")
public class TestObject {
    private ClassB classBObject;
    private List<ClassB> classBCollection;

    public TestObject(){
        classBCollection = new ArrayList<ClassB>();
    }

    public ClassB getClassBObject() {
        return classBObject;
    }

    public void setClassBObject(ClassB classBObject) {
        this.classBObject = classBObject;
    }

    public List<ClassB> getClassBCollection() {
        return classBCollection;
    }

    public void setClassBCollection(List<ClassB> classBCollection) {
        this.classBCollection = classBCollection;
    }

    public boolean equals(Object theObject){
        if(!(theObject instanceof TestObject)){
            return false;
        }
        if(!getClassBObject().equals(((TestObject)theObject).getClassBObject())){
            return false;
        }
        if(getClassBCollection().size() != ((TestObject)theObject).getClassBCollection().size()){
            return false;
        }
        for(int i=0;i<getClassBCollection().size(); i++){
            if(!getClassBCollection().get(i).equals(((TestObject)theObject).getClassBCollection().get(i))){
                return false;
            }
        }

        return true;
    }
}
