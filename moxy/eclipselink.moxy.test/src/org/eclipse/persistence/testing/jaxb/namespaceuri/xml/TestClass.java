/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Matt MacIvor - 2.3.3
package org.eclipse.persistence.testing.jaxb.namespaceuri.xml;

import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement
public class TestClass {

    public String foo;

    @XmlPath("foo/@xml:lang")
    public String lang;

    public boolean equals(Object obj) {
        TestClass tc = (TestClass)obj;
        return tc.foo.equals(foo) && tc.lang.equals(lang);
    }

}
