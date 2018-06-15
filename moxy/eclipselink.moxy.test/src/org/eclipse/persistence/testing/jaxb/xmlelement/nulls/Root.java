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
//     Denise Smith - 2.4 - January 2013
package org.eclipse.persistence.testing.jaxb.xmlelement.nulls;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.jaxb.xmlelement.model.Address;

@XmlRootElement
public class Root {
    private String string1; //default
    private String string2; //nillable=true
    private String string3; //deafult
    private Child child1; //default
    private Child child2;//nillable=true
    private List<Child> childList1; //default
    private List<Child> childList2;//nillable=true
    private List<Child> childList3; //default
    private List<Child> childList4;//nillable=true
    private List<String> stringList1;//default
    private List<String> stringList2;//nillable=true
    private List<String> stringList3;//default
    private List<String> stringList4;//nillable=true
    private List<byte[]> byteArrayList1;//nillable=false
    private List<byte[]> byteArrayList2;//nillable=true
    private List<byte[]> byteArrayList3;//nillable=false
    private List<byte[]> byteArrayList4;//nillable=true
    private List<Object> refList1;//nillable=false
    private List<Object> refList2;//nillable=true
    private List<Object> refList3;//nillable=false
    private List<Object> refList4;//nillable=true
    private List<Object> refComplexList1;//nillable=false
    private List<Object> refComplexList2;//nillable=true
    private List<Object> refComplexList3;//nillable=false
    private List<Object> refComplexList4;//nillable=true

    @XmlElement(nillable=false)
    public List<Child> getChildList3() {
        return childList3;
    }
    public void setChildList3(List<Child> childList3) {
        this.childList3 = childList3;
    }
    @XmlElement(nillable=true)
    public List<Child> getChildList4() {
        return childList4;
    }
    public void setChildList4(List<Child> childList4) {
        this.childList4 = childList4;
    }

    @XmlElement(nillable=false)
    public String getString1() {
        return string1;
    }
    public void setString1(String string1) {
        this.string1 = string1;
    }
    @XmlElement(nillable=true)
    public String getString2() {
        return string2;
    }
    public void setString2(String string2) {
        this.string2 = string2;
    }

    public String getString3() {
        return string3;
    }
    public void setString3(String string3) {
        this.string3 = string3;
    }
    @XmlElement(nillable=false)
    public Child getChild1() {
        return child1;
    }
    public void setChild1(Child child1) {
        this.child1 = child1;
    }
    @XmlElement(nillable=true)
    public Child getChild2() {
        return child2;
    }
    public void setChild2(Child child2) {
        this.child2 = child2;
    }
    @XmlElement(nillable=false)
    public List<Child> getChildList1() {
        return childList1;
    }
    public void setChildList1(List<Child> childList1) {
        this.childList1 = childList1;
    }
    @XmlElement(nillable=true)
    public List<Child> getChildList2() {
        return childList2;
    }
    public void setChildList2(List<Child> childList2) {
        this.childList2 = childList2;
    }
    @XmlElement(nillable=false)
    public List<String> getStringList1() {
        return stringList1;
    }
    public void setStringList1(List<String> stringList1) {
        this.stringList1 = stringList1;
    }
    @XmlElement(nillable=true)
    public List<String> getStringList2() {
        return stringList2;
    }
    public void setStringList2(List<String> stringList2) {
        this.stringList2 = stringList2;
    }

    @XmlElement(nillable=false)
    public List<String> getStringList3() {
        return stringList3;
    }
    public void setStringList3(List<String> stringList3) {
        this.stringList3 = stringList3;
    }
    @XmlElement(nillable=true)
    public List<String> getStringList4() {
        return stringList4;
    }
    public void setStringList4(List<String> stringList4) {
        this.stringList4 = stringList4;
    }
    @XmlElement(nillable=false)
    public List<byte[]> getByteArrayList1() {
        return byteArrayList1;
    }
    public void setByteArrayList1(List<byte[]> byteArrayList1) {
        this.byteArrayList1 = byteArrayList1;
    }
    @XmlElement(nillable=true)
    public List<byte[]> getByteArrayList2() {
        return byteArrayList2;
    }
    public void setByteArrayList2(List<byte[]> byteArrayList2) {
        this.byteArrayList2 = byteArrayList2;
    }
    @XmlElement(nillable=false)
    public List<byte[]> getByteArrayList3() {
        return byteArrayList3;
    }
    public void setByteArrayList3(List<byte[]> byteArrayList3) {
        this.byteArrayList3 = byteArrayList3;
    }
    @XmlElement(nillable=true)
    public List<byte[]> getByteArrayList4() {
        return byteArrayList4;
    }
    public void setByteArrayList4(List<byte[]> byteArrayList4) {
        this.byteArrayList4 = byteArrayList4;
    }

    @XmlElements({@XmlElement(name="integerchoice1", type=Integer.class, nillable=false), @XmlElement(name="stringchoice1", type=String.class, nillable=false)})
    public List<Object> getRefList1() {
        return refList1;
    }

    public void setRefList1(List<Object> refList1) {
        this.refList1 = refList1;
    }

    //@XmlElement(nillable=true)
    @XmlElements({@XmlElement(name="integerchoice2", type=Integer.class, nillable=true), @XmlElement(name="stringchoice2", type=String.class, nillable=true)})
    public List<Object> getRefList2() {
        return refList2;
    }
    public void setRefList2(List<Object> refList2) {
        this.refList2 = refList2;
    }

    //@XmlElement(nillable=false)
    @XmlElements({@XmlElement(name="integerchoice3", type=Integer.class, nillable=false), @XmlElement(name="stringchoice3", type=String.class, nillable=false)})

    public List<Object> getRefList3() {
        return refList3;
    }
    public void setRefList3(List<Object> refList3) {
        this.refList3 = refList3;
    }

    //@XmlElement(nillable=true)
    @XmlElements({@XmlElement(name="integerchoice4", type=Integer.class, nillable=true), @XmlElement(name="stringchoice4", type=String.class, nillable=true)})
    public List<Object> getRefList4() {
        return refList4;
    }
    public void setRefList4(List<Object> refList4) {
        this.refList4 = refList4;
    }

    @XmlElements({@XmlElement(name="somethingchoice1", type=Something.class, nillable=false), @XmlElement(name="somethingelsechoice1", type=SomethingElse.class, nillable=false)})
    public List<Object> getRefComplexList1() {
        return refComplexList1;
    }
    public void setRefComplexList1(List<Object> refComplexList1) {
        this.refComplexList1 = refComplexList1;
    }
    @XmlElements({@XmlElement(name="somethingchoice2", type=Something.class, nillable=true), @XmlElement(name="somethingelsechoice2", type=SomethingElse.class, nillable=true)})
    public List<Object> getRefComplexList2() {
        return refComplexList2;
    }
    public void setRefComplexList2(List<Object> refComplexList2) {
        this.refComplexList2 = refComplexList2;
    }
    @XmlElements({@XmlElement(name="somethingchoice3", type=Something.class, nillable=false), @XmlElement(name="somethingelsechoice3", type=SomethingElse.class, nillable=false)})
    public List<Object> getRefComplexList3() {
        return refComplexList3;
    }
    public void setRefComplexList3(List<Object> refComplexList3) {
        this.refComplexList3 = refComplexList3;
    }
    @XmlElements({@XmlElement(name="somethingchoice4", type=Something.class, nillable=true), @XmlElement(name="somethingelsechoice4", type=SomethingElse.class, nillable=true)})
    public List<Object> getRefComplexList4() {
        return refComplexList4;
    }
    public void setRefComplexList4(List<Object> refComplexList4) {
        this.refComplexList4 = refComplexList4;
    }
    public boolean equals(Object obj){
        if(!(obj instanceof Root)){
            return false;
        }
        Root compareObject = (Root)obj;
        if(child1 == null){
            if(compareObject.child1 !=null){
                return false;
            }
        }else if(!child1.equals(compareObject.child1)){
            return false;
        }

        if(child2 == null){
            if(compareObject.child2 !=null){
                return false;
            }
        }else if(!child2.equals(compareObject.child2)){
            return false;
        }

        if((string1 == null && compareObject.string1 !=null)   || (string1 != null && !string1.equals(string2))){
            return false;
        }

        if((string2 == null && compareObject.string2 !=null)   || (string2 != null && !string2.equals(string2))){
            return false;
        }
        if((string3 == null && compareObject.string3 !=null)   || (string3 != null && !string3.equals(string3))){
            return false;
        }
        if((childList1 == null && compareObject.childList1 !=null)   || (childList1 != null && !childList1.equals(childList1))){
            return false;
        }
        if((childList2 == null && compareObject.childList2 !=null)   || (childList2 != null && !childList2.equals(childList2))){
            return false;
        }
        if((childList3 == null && compareObject.childList3 !=null)   || (childList3 != null && !childList3.equals(childList3))){
            return false;
        }
        if((childList4 == null && compareObject.childList4 !=null)   || (childList4 != null && !childList4.equals(childList4))){
            return false;
        }
        if((stringList1 == null && compareObject.stringList1 !=null)   || (stringList1 != null && !stringList1.equals(stringList1))){
            return false;
        }
        if((stringList2 == null && compareObject.stringList2 !=null)   || (stringList2 != null && !stringList2.equals(stringList2))){
            return false;
        }
        if((stringList3 == null && compareObject.stringList3 !=null)   || (stringList3 != null && !stringList3.equals(stringList3))){
            return false;
        }
        if((stringList4 == null && compareObject.stringList4 !=null)   || (stringList4 != null && !stringList4.equals(stringList4))){
            return false;
        }
        if((byteArrayList1 == null && compareObject.byteArrayList1 !=null)   || (byteArrayList1 != null && !byteArrayList1.equals(byteArrayList1))){
            return false;
        }
        if((byteArrayList2 == null && compareObject.byteArrayList2 !=null)   || (byteArrayList2 != null && !byteArrayList2.equals(byteArrayList2))){
            return false;
        }
        if((byteArrayList3 == null && compareObject.byteArrayList3 !=null)   || (byteArrayList3 != null && !byteArrayList3.equals(byteArrayList3))){
            return false;
        }
        if((byteArrayList4 == null && compareObject.byteArrayList4 !=null)   || (byteArrayList4 != null && !byteArrayList4.equals(byteArrayList4))){
            return false;
        }
        if((refList1 == null && compareObject.refList1 !=null)   || (refList1 != null && !refList1.equals(refList1))){
            return false;
        }
        if((refList2 == null && compareObject.refList2 !=null)   || (refList2 != null && !refList2.equals(refList2))){
            return false;
        }
        if((refList3 == null && compareObject.refList3 !=null)   || (refList3 != null && !refList3.equals(refList3))){
            return false;
        }
        if((refList4 == null && compareObject.refList4 !=null)   || (refList4 != null && !refList4.equals(refList4))){
            return false;
        }
        if((refComplexList1 == null && compareObject.refComplexList1 !=null)   || (refComplexList1 != null && !refComplexList1.equals(refComplexList1))){
            return false;
        }
        if((refComplexList2 == null && compareObject.refComplexList2 !=null)   || (refComplexList2 != null && !refComplexList2.equals(refComplexList2))){
            return false;
        }
        if((refComplexList3 == null && compareObject.refComplexList3 !=null)   || (refComplexList3 != null && !refComplexList3.equals(refComplexList3))){
            return false;
        }
        if((refComplexList4 == null && compareObject.refComplexList4 !=null)   || (refComplexList4 != null && !refComplexList4.equals(refComplexList4))){
            return false;
        }
        return true;
    }

}
