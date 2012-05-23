/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.2 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmlanyelement.ns;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.w3c.dom.Element;

@XmlRootElement
public class Root {

    private Element child;

    @XmlAnyElement
    public Element getChild() {
        return child;
    }

    public void setChild(Element child) {
        this.child = child;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != Root.class) {
            return false;
        }
        Root test = (Root) obj;
        try {
            OXTestCase oxTestCase = new DefaultNamespaceTestCases("");
            Element testChild = test.getChild();
            if(null == child) {
                return null == testChild;
            }
            return true;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}