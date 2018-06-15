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
// Vikram Bhatia - Initial implementation
package org.eclipse.persistence.testing.jaxb.stax;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.w3c.dom.Element;

@XmlRootElement(name="employee")
public class EmployeeLax {

    @XmlAnyElement(lax = true)
    protected Element child;

    public Element getChild() {
        return child;
    }

    public void setChild(Element value) {
        this.child = value;
    }
}
