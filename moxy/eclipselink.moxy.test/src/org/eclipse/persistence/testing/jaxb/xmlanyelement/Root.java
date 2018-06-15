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
package org.eclipse.persistence.testing.jaxb.xmlanyelement;

import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Node;

@XmlRootElement
public class Root {

    @XmlAnyElement(lax=true)
    public List things;

    public boolean equals(Object obj){
        if(obj instanceof Root){
            for(int i=0; i<things.size(); i++){
                Object obj1 = things.get(i);
                Object obj2 = ((Root)obj).things.get(i);
                if(obj1 instanceof Node && obj2 instanceof Node){
                    XMLComparer comparer = new XMLComparer();
                    if(!(comparer.isNodeEqual((Node)obj1, (Node)obj2))){
                        return false;
                    }
                }else if (!obj1.equals(obj2)){
                    return false;
                }

            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return things != null ? things.hashCode() : 0;
    }
}
