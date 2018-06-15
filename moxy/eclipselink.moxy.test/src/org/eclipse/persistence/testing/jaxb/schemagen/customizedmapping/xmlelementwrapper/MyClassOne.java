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
// dmccann - May 21/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.schemagen.customizedmapping.xmlelementwrapper;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "root", namespace = "http://www.example.com/One")
@XmlType(namespace = "http://www.example.com/One")
class MyClassOne {
    @XmlElementWrapper(name = "name-wrapper", namespace = "http://www.example.com/Two")
    @XmlElement(name = "names")
    public String[] names;

    @XmlElement(name = "comment", namespace = "http://www.example.com/Two")
    public String myStr;
}
