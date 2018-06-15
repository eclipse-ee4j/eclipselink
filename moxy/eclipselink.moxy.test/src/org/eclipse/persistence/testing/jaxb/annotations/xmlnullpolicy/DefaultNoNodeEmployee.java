/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.4.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlnullpolicy;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "root")
public class DefaultNoNodeEmployee {

    String elementString;
    String elementStringNillable;
    DefaultNoNodeEmployee elementPOJO;
    DefaultNoNodeEmployee elementPOJONillable;
    String attribute;
    Object any;
    List choiceList;
    DefaultNoNodeEmployee reference;
    DefaultNoNodeEmployee choice;

    boolean wasSetElementString;
    boolean wasSetElementStringNillable;
    boolean wasSetElementPOJO;
    boolean wasSetElementPOJONillable;
    boolean wasSetAttribute;
    boolean wasSetAny;
    boolean wasSetReference;
    boolean wasSetChoice;
    boolean wasSetChoiceList;

    public DefaultNoNodeEmployee(){
    }

    @XmlAnyElement
    public Object getAny() {
        return any;
    }

    @XmlAttribute
    @XmlID
    public String getAttribute() {
        return attribute;
    }

    @XmlElements({
        @XmlElement(name="foo", type=DefaultNoNodeEmployee.class),
        @XmlElement(name="bar", type=DefaultNoNodeEmployee.class)
    })
    public DefaultNoNodeEmployee getChoice() {
        return choice;
    }



    @XmlElements({
        @XmlElement(name="foo", type=DefaultNoNodeEmployee.class),
        @XmlElement(name="bar", type=DefaultNoNodeEmployee.class)
    })
    public List getChoiceList() {
        return choiceList;
    }

    public DefaultNoNodeEmployee getElementPOJO() {
        return elementPOJO;
    }

    public DefaultNoNodeEmployee getElementPOJONillable() {
        return elementPOJONillable;
    }

    public String getElementString() {
        return elementString;
    }

    public String getElementStringNillable() {
        return elementStringNillable;
    }

    @XmlIDREF
    public DefaultNoNodeEmployee getReference() {
        return reference;
    }

    public void setAny(Object any) {
        this.any = any;
        wasSetAny = true;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
        wasSetAttribute = true;
    }

    public void setChoice(DefaultNoNodeEmployee choice) {
        this.choice = choice;
        wasSetChoice = true;
    }

    public void setChoiceList(List choiceList) {
        this.choiceList = choiceList;
        wasSetChoiceList = true;
    }

    public void setElementPOJO(DefaultNoNodeEmployee element) {
        this.elementPOJO = element;
        wasSetElementPOJO = true;
    }

    public void setElementPOJONillable(DefaultNoNodeEmployee element) {
        this.elementPOJONillable = element;
        wasSetElementPOJONillable = true;
    }

    public void setElementString(String element) {
        this.elementString = element;
        wasSetElementString = true;
    }

    public void setElementStringNillable(String element) {
        this.elementStringNillable = element;
        wasSetElementStringNillable = true;
    }

    public void setReference(DefaultNoNodeEmployee reference) {
        this.reference = reference;
        wasSetReference = true;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj) {
            return false;
        }
        try {
            DefaultNoNodeEmployee test = (DefaultNoNodeEmployee) obj;

            if(any != test.any || wasSetAny != test.wasSetAny) {
                return false;
            }

            if(attribute != test.attribute || wasSetAttribute != test.wasSetAttribute) {
                return false;
            }

            if(choice != test.choice || wasSetChoice != test.wasSetChoice) {
                return false;
            }

            if(choiceList != test.choiceList || wasSetChoiceList != test.wasSetChoiceList) {
                return false;
            }

            if(elementString != test.elementString || wasSetElementString != test.wasSetElementString) {
                return false;
            }
            if(elementStringNillable != test.elementStringNillable || wasSetElementStringNillable != test.wasSetElementStringNillable) {
                return false;
            }
            if(elementPOJO != test.elementPOJO || wasSetElementPOJO != test.wasSetElementPOJO) {
                return false;
            }
            if(elementPOJONillable != test.elementPOJONillable || wasSetElementPOJONillable != test.wasSetElementPOJONillable) {
                return false;
            }
            if(reference != test.reference || wasSetReference != test.wasSetReference) {
                return false;
            }
            return true;
        } catch(ClassCastException e) {
            return false;
        }
    }

}
