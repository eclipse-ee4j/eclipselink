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
// Denise Smith - August 2013

package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs3;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlanyelement.xmlelementrefs.Foo;
import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Node;

@XmlRootElement(name="foo")
public class FooImpl implements Foo {
    private List<Object> others;

    @XmlAnyElement(lax=true)
    @XmlElementRefs({
        @XmlElementRef(name="a", type=JAXBElement.class),
        @XmlElementRef(name="b", type=JAXBElement.class)
    })
    @XmlMixed
    public List<Object> getOthers() {
        return others;
    }

    public void setOthers(List<Object> others) {
        this.others = others;
    }

    public boolean equals(Object obj){
        if(!(obj instanceof FooImpl)){
            return false;
        }
        FooImpl fooObj = (FooImpl)obj;
        if(others.size() != fooObj.others.size()){
            return false;
        }

        XMLComparer comparer = new XMLComparer();
        for(int i =0;i <others.size(); i++){
            Object o1 = others.get(i);
            Object o2 = fooObj.others.get(i);
            if(o1 instanceof Node){
                if(!(o2 instanceof Node)){
                    return false;
                }
                if(!comparer.isNodeEqual((Node)o1, (Node)o2)){
                    return false;
                }
            }else if(o1 instanceof JAXBElement){
                if(!(o2 instanceof JAXBElement)){
                    return false;
                }
                JAXBElement one = (JAXBElement)o1;
                JAXBElement two = (JAXBElement)o2;
                if(!one.getName().equals(two.getName())){
                    return false;
                }
                if(!one.getValue().equals(two.getValue())){
                    return false;
                }

            }else if(!o1.equals(o2)){
                return false;
            }
        }

        return true;
    }
}
