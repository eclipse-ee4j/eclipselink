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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.jaxb.xmlschematype;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="employee")
public class EmployeeDate {

    @XmlElement(name="start-date")
    @XmlSchemaType(name = "date")
    public java.util.Calendar startDate;

    public boolean equals(Object o) {
        if(!(o instanceof EmployeeDate) || o == null) {
            return false;
        } else {
            if(startDate == null){
                return  ((EmployeeDate)o).startDate == null;
            }else{
                return ((EmployeeDate)o).startDate.getTimeInMillis() == this.startDate.getTimeInMillis();
            }
        }
    }

    public String toString() {
        return "EMPLOYEE(" + startDate + ")";
    }
}
