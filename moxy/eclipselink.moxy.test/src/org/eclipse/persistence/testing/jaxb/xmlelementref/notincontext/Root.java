/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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

