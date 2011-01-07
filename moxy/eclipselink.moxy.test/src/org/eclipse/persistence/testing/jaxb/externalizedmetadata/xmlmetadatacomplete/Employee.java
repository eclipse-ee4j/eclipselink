/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - November 09/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlmetadatacomplete;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

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
