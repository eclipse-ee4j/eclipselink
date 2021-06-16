/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - February 20, 2013
package org.eclipse.persistence.testing.jaxb.xmlidref.xmlelements.wrapper;

import java.util.*;
import jakarta.xml.bind.annotation.*;

@XmlRootElement
public class Foo {

    @XmlElements({
        @XmlElement(name="Attribute1", type=AttributeImpl.class),
        @XmlElement(name="Attribute2", type=AttributeImpl2.class)
    })
    List<Attribute> attributes = new ArrayList<Attribute>();;

    @XmlElementWrapper(name="AttributeRefs")
    @XmlElements({
      @XmlElement(name="AttributeRef1", type=AttributeImpl.class),
      @XmlElement(name="AttributeRef2", type=AttributeImpl2.class)
    })
    @XmlIDREF
    List<Attribute> attributeRefs = new ArrayList<Attribute>();

    @XmlElementWrapper(name="AttributeImplsWrapper")
    @XmlIDREF
    List<AttributeImpl> attributeImplRefs = new ArrayList<AttributeImpl>();

    public boolean equals(Object obj){
        if(obj instanceof Foo){
            Foo compare = (Foo)obj;
            if(!attributes.equals(compare.attributes)){
                return false;
            }
            if(!attributeRefs.equals(compare.attributeRefs)){
                return false;
            }
            if(!attributeImplRefs.equals(compare.attributeImplRefs)){
                return false;
            }
            return true;
        }
        return false;
    }

}
