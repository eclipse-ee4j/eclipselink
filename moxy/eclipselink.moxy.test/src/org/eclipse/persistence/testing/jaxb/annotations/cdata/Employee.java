/*******************************************************************************
* Copyright (c) 2011, 2016 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     mmacivor - Initial implementation
******************************************************************************/
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
