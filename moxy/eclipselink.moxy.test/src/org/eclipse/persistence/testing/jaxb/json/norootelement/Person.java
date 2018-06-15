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
//     Denise Smith - 2.4.2
package org.eclipse.persistence.testing.jaxb.json.norootelement;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement
@XmlSeeAlso(Customer.class)
public class Person {

    public String name;

    public boolean equals(Object obj){
        if(obj instanceof Person){
            String otherName = ((Person)obj).name;
            return name == null && otherName == null || name.equals(otherName);
        }else{
            return false;
        }
    }
}
