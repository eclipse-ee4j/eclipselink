/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.xmlpath;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;
@XmlRootElement(name="root")
public class TestObjectToElementWithXmlAttribute {

    @XmlAttribute
    @XmlPath("something/theFlag/text()")
    public boolean theFlag;

    public boolean equals(Object obj){
        if(obj instanceof TestObjectToElementWithXmlAttribute){
            return theFlag == ((TestObjectToElementWithXmlAttribute)obj).theFlag;
        }
        return false;
    }
}
