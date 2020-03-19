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
// dmccann - November 09/2010 - 2.2 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmetadatacomplete;

import java.util.List;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({XmlMetadataCompleteTestCases.class})
@XmlRootElement(name="root-element-should-be-ignored")
public class Employee {
    private int id;

    @XmlElementWrapper(name="items")
    @XmlElements({
        @XmlElement(name="A", type=Integer.class),
        @XmlElement(name="B", type=Float.class)
    })
    public List<Object> things;

    @XmlElementRefs({
        @XmlElementRef(name="foo", type=JAXBElement.class),
        @XmlElementRef(name="bar", type=JAXBElement.class)
    })
    public List<JAXBElement<String>> fooOrBar;

    @XmlAttribute(name="attribute-should-be-ignored")
    public String name;

    @XmlElement(name="element-should-be-ignored")
    public int getId() {
        return id;
    }

    public void setId(int newId) {
        id = newId;
    }
}
