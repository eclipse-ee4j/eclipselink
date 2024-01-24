/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
// dmccann - June 17/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlcustomizer;

import jakarta.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlCustomizer;

@XmlCustomizer(value=MyEmployeeCustomizer.class)
@XmlRootElement(name="employee")
public class Employee {
    public String firstName;
    public String lastName;

    public boolean equals(Object obj) {
        if (!(obj instanceof Employee empObj)) {
            return false;
        }
        return (empObj.firstName.equals(this.firstName) && empObj.lastName.equals(this.lastName));
    }
}
