/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.jaxb.security.xss;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "myroot")
public class MyRoot {

    private String elem1;

    public MyRoot() {
    }

    public MyRoot(String elem1) {
        this.elem1 = elem1;
    }

    @XmlElement(name = "elem1")
    public String getElem1() {
        return elem1;
    }

    public void setElem1(String elem1) {
        this.elem1 = elem1;
    }

    @Override
    public String toString() {
        return "MyRoot{" +
                "elem1='" + elem1 + '\'' +
                '}';
    }
}
