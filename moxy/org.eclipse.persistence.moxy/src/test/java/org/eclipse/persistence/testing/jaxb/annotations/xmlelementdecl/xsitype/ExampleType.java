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
//     Denise Smith - September 2013
package org.eclipse.persistence.testing.jaxb.annotations.xmlelementdecl.xsitype;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Node;

@XmlType(name = "ExampleType")
public class ExampleType {

    @XmlElement(name = "Content")
    public Object content;

    public boolean equals(Object obj) {
        if(obj instanceof ExampleType){
            if(content != null && content instanceof Node){
                XMLComparer comparer = new XMLComparer();
                return comparer.isNodeEqual((Node)content,(Node) ((ExampleType)obj).content);
            }
            return (content == null && ((ExampleType)obj).content == null)
             || (content != null && content.equals(((ExampleType)obj).content));
        }
        return false;
    }
}


