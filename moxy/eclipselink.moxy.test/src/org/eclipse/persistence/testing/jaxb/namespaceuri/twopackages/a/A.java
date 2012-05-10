/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages.a;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.testing.jaxb.namespaceuri.twopackages.b.B;

@XmlRootElement
public class A {

    private B b;

    @XmlElement(namespace="http://www.example.com/B")
    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        A test = (A) obj;
        if(null == b) {
            return null == test.getB();
        } else {
            return b.equals(test.getB());
        }
    }

}