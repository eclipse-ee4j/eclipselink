/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse -01 March 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.inheritance;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
