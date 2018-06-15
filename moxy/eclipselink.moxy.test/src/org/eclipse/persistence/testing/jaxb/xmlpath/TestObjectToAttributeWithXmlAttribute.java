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
// Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.xmlpath;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(name="root")
public class TestObjectToAttributeWithXmlAttribute {
    @XmlAttribute
    @XmlPath("something/@theFlag")
    public boolean theFlag;

    public boolean equals(Object obj){
        if(obj instanceof TestObjectToAttributeWithXmlAttribute){
            return theFlag == ((TestObjectToAttributeWithXmlAttribute)obj).theFlag;
        }
        return false;
    }
}
