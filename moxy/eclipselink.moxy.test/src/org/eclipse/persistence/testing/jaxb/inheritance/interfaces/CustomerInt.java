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
//  -Matt MacIvor - Initial Implementation - 2.4.1
package org.eclipse.persistence.testing.jaxb.inheritance.interfaces;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="customer")
public interface CustomerInt extends CustomerBaseInt {
    @XmlElement
    public String getLastName();

    public void setLastName(String lName);

}
