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
// dmccann - July 09/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.inheritance;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public class Person {
    public String name;
    public int age;

    public boolean equals(Object obj){
        if(obj instanceof Person){
            Person personObj = (Person)obj;
            return name.equals(personObj.name) && age == personObj.age;
        }
        return false;
    }
}
