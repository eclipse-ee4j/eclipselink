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
// Denise Smith - 2.3

package org.eclipse.persistence.testing.jaxb.jaxbcontext.withjaxbindex;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ClassA {
    private Object theValue;
    public List<String> theValues;

    public ClassA(){
        theValues = new ArrayList<String>();
    }

    public Object getTheValue() {
        return theValue;
    }

    public void setTheValue(Object theValue) {
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
            if(getTheValue() instanceof JAXBElement){
                if(!(classAObj.getTheValue() instanceof JAXBElement)){
                    return false;
                }else{
                    JAXBElement jb1 = (JAXBElement)getTheValue();
                    JAXBElement jb2 = (JAXBElement)classAObj.getTheValue();
                    if(!jb1.getValue().equals(jb2.getValue())){
                        return false;
                    }
                    if(!jb1.getName().equals(jb2.getName())){
                        return false;
                    }
                }
            }else if(!getTheValue().equals(classAObj.getTheValue())){
                return false;
            }
        }




        /*else{
            if(classAObj.getTheValue() == null){
                return false;
            }
            if(!getTheValue().equals(classAObj.getTheValue())){
                return false;
            }
        }    */

        return true;
    }
}
