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
//     Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.xmlelementref.notincontext;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class Root {

    @XmlElementRefs({
            @XmlElementRef(name = "foo", type = Foo.class),
            @XmlElementRef(name = "bar", type = Bar.class)
        })
    public List content = new ArrayList();

    public boolean equals(Object obj){
        if(obj instanceof Root){
            Root rootObj = (Root)obj;
            if(content.size() != rootObj.content.size()){
                return false;
            }
            return (content.containsAll(rootObj.content) && rootObj.content.containsAll(content));
        }
        return false;
    }
}

