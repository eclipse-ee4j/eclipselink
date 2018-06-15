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
package org.eclipse.persistence.testing.jaxb.casesensitivity.correctCase;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Implementation of Customer with XML Elements and Attributes with the same case as in the xml resource.
 *
 * @author Marcel Valovy - marcel.valovy@oracle.com
 */
@XmlRootElement
public class CustomerImpl extends org.eclipse.persistence.testing.jaxb.casesensitivity.Customer {

    private int id;
    private int age;
    private String personalName;
    private String personalname; /* collision - this one prevails */

    public int getId() {
        return id;
    }

    @XmlAttribute
    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    @XmlAttribute
    public void setAge(int age) {
        this.age = age;
    }

    public String getPersonalName() {
        return personalName;
    }

    @XmlElement
    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public String getPersonalname() {
        return personalname;
    }

    @XmlElement
    public void setPersonalname(String personalname) {
        this.personalname = personalname;
    }

    @Override
    public String toString() {
        return "CustomerImpl_correctCase{" +
                "id=" + id +
                ", age=" + age +
                ", personalname='" + personalname + '\'' +
                ", personalName='" + personalName + '\'' +
                '}';
    }

    @Override
    public int getIdBridge() {
        return id;
    }

    @Override
    public int getAgeBridge() {
        return age;
    }

    @Override
    public String getNameBridge() {
        return personalname;
    }

}
