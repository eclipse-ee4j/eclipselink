/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Matt MacIvor - 2.4.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmlelement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
