/*******************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Marcel Valovy - 2.6.0 - initial implementation
 ******************************************************************************/
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
