/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     mmacivor - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.cdata;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlCDATA;

@XmlRootElement(name="employee")
public class Employee {
    @XmlElement
    public String name;

    @XmlElement
    @XmlCDATA
    public String xmlData;

    @XmlElement
    @XmlCDATA
    public String nestedCData;

    @XmlElement
    @XmlCDATA
    public String anotherCData1;

    @XmlElement
    @XmlCDATA
    public String anotherCData2;

    @XmlElement
    @XmlCDATA
    public String anotherCData3;

    public boolean equals(Object obj) {
        if(obj instanceof Employee) {
            return name.equals(((Employee)obj).name)
                    && xmlData.equals(((Employee)obj).xmlData)
                    && nestedCData.equals(((Employee)obj).nestedCData)
                    && anotherCData1.equals(((Employee)obj).anotherCData1)
                    && anotherCData2.equals(((Employee)obj).anotherCData2)
                    && anotherCData3.equals(((Employee)obj).anotherCData3);
        }
        return false;
    }

}
