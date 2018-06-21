/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse -01 March 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.inheritance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Root {

    @XmlJavaTypeAdapter(FooAdapter.class)
    protected Foo foo;

    public boolean equals(Object obj) {
        if (obj instanceof Root) {
            Foo aFoo = ((Root) obj).foo;
            if (aFoo == null && this.foo == null) {
                return true;
            }
            if (aFoo != null && this.foo != null) {
                return true;
            }
        }
        return false;
    }

}
