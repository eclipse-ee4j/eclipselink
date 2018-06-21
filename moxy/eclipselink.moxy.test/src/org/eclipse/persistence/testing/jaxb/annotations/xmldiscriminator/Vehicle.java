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
// dmccann - August 24/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmldiscriminator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorNode;
import org.eclipse.persistence.oxm.annotations.XmlDiscriminatorValue;

@XmlRootElement(name = "vehicle-data")
@XmlDiscriminatorNode("@vtype")
@XmlDiscriminatorValue("vehicle")
public class Vehicle {
    public String model;
    public String manufacturer;
    @XmlElement(name = "top-speed")
    public int topSpeed;
}
