/*******************************************************************************
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlanyelement.ns.qualified;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.w3c.dom.Node;

@XmlRootElement(name="root", namespace="")
public class DefaultNamespaceRoot {

    @XmlAnyElement(lax=true)
    public Object any;

    @Override
    public boolean equals(Object obj)  {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        DefaultNamespaceRoot test = (DefaultNamespaceRoot) obj;
        
        Node controlDOM = (Node) any;
        Node testDOM = (Node) test.any;
        
        XMLComparer comparer = new XMLComparer();
        return comparer.isNodeEqual(controlDOM, testDOM);
    }

}
