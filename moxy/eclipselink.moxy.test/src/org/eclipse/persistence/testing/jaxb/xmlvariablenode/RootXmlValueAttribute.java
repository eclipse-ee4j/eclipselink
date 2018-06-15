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
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlVariableNode;

@XmlRootElement(name="root")
public class RootXmlValueAttribute {

    public String name;

    @XmlVariableNode(value= "thingName", attribute=true)
    //@XmlAttribute
    public ThingXmlValue thing;

    public boolean equals(Object obj){
        if(obj instanceof RootXmlValueAttribute){
            return thing.equals(((RootXmlValueAttribute)obj).thing) &&
            name.equals(((RootXmlValueAttribute)obj).name);
        }
        return false;
    }
}
