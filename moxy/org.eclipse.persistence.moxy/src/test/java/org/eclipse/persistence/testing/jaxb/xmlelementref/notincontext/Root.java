/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.xmlelementref.notincontext;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlRootElement;

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

