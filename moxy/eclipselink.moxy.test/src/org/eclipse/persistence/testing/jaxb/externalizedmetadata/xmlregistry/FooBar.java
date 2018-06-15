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
// dmccann - January 20/2010 - 2.0 - Initial implementation
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlregistry;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

public class FooBar {
    //@XmlElementRefs({
    //    @XmlElementRef(name="foo",type=JAXBElement.class),
    //    @XmlElementRef(name="bar",type=JAXBElement.class)
    //})
    public List<JAXBElement<String>> fooOrBar;

    public boolean equals (Object obj){
        if(obj instanceof FooBar){
            return true;
        }
        return false;
    }
}
