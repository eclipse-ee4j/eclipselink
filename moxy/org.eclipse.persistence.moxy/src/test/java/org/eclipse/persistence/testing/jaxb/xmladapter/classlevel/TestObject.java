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
// Denise Smith - September 10 /2009
package org.eclipse.persistence.testing.jaxb.xmladapter.classlevel;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name="testObject")
public class TestObject {
    private ClassB classBObject;
    private List<ClassB> classBCollection;
    private ClassBSubClass subClassObject;
    @XmlJavaTypeAdapter(ClassCtoClassBAdapter.class)
    public ClassB anotherClassBObject;

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
        if(!getSubClassObject().equals(((TestObject)theObject).getSubClassObject())){
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

    public ClassBSubClass getSubClassObject() {
        return subClassObject;
    }

    public void setSubClassObject(ClassBSubClass subClassObject) {
        this.subClassObject = subClassObject;
    }

}
