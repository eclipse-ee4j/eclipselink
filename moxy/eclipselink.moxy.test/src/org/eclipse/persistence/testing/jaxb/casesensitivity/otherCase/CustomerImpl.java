/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Marcel Valovy - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.jaxb.casesensitivity.otherCase;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Implementation of Customer with XML Elements and Attributes in case differing from that in the xml resource.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
@XmlRootElement
public class CustomerImpl extends org.eclipse.persistence.testing.jaxb.casesensitivity.Customer {

    private int ID;
    private int age;
    private String personalNAME; /* collision - this one prevails */
    private String persoNalNaMe;

    public int getID() {
        return ID;
    }

    @XmlAttribute
    public void setID(int ID) {
        this.ID = ID;
    }

    public int getAge() {
        return age;
    }

    /* Attribute left Camel Case on purpose, to provide safe-point during debugging. */
    @XmlAttribute
    public void setAge(int age) {
        this.age = age;
    }

    public String getPersonalNAME() {
        return personalNAME;
    }

    @XmlElement
    public void setPersonalNAME(String personalNAME) {
        this.personalNAME = personalNAME;
    }

    public String getPersoNalNaMe() {
        return persoNalNaMe;
    }

    @XmlElement
    public void setPersoNalNaMe(String persoNalNaMe) {
        this.persoNalNaMe = persoNalNaMe;
    }

    @Override
    public String toString() {
        return "CustomerImpl_otherCase{" +
                "ID=" + ID +
                ", age=" + age +
                ", persoNalNaMe='" + persoNalNaMe + '\'' +
                ", personalNAME='" + personalNAME + '\'' +
                '}';
    }

    @Override
    public int getIdBridge() {
        return ID;
    }

    @Override
    public int getAgeBridge() {
        return age;
    }

    @Override
    public String getNameBridge() {
        return personalNAME;
    }

}
