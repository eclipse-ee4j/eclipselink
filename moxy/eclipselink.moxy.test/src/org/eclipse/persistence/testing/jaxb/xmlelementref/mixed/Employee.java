/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// mmacivor - May 31, 2010 - 2.1 - Initial implementation

package org.eclipse.persistence.testing.jaxb.xmlelementref.mixed;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="employee")
public class Employee {

    @XmlElementRef(name="task", type=Task.class)
    @XmlMixed
    public List<Object> tasks;

    public boolean equals(Object obj) {
        if(!(obj instanceof Employee)) {
            return false;
        }

        return this.tasks.equals(((Employee)obj).tasks);
    }
}
