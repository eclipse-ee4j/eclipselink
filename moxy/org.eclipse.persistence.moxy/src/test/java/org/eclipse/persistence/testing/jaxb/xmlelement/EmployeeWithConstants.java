/*
 * Copyright (c) 2012, 2020 Oracle and/or its affiliates. All rights reserved.
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
//     Matt MacIvor - 2.4.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelement;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="employee-data")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmployeeWithConstants {

    @XmlElement(name = "constant")
    private static final String ANNOTATED_CONSTANT= "VALUE";

    private static final String UN_ANNOTATED_CONSTANT = "ANOTHER VALUE";

    public String id;

    public boolean equals(Object obj) {
        return this.id.equals(((EmployeeWithConstants)obj).id);
    }
}
