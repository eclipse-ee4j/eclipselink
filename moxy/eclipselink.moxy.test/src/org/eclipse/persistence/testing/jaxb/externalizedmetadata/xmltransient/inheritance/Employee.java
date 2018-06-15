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
// dmccann - July 09/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmltransient.inheritance;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Employee extends Person {
    public int employeeId;

    public boolean equals(Object obj){
        if(obj instanceof Employee){
            if(! super.equals(obj)){
                return false;
            }
            return employeeId == ((Employee)obj).employeeId;
        }
        return false;
    }
}
