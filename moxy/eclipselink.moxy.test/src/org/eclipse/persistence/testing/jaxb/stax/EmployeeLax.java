/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Vikram Bhatia - Initial implementation
 ******************************************************************************/
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
