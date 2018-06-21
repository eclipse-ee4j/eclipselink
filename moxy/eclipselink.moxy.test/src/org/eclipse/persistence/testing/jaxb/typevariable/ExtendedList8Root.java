/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     Blaise Doughan - 2.5.2 - initial implementation
package org.eclipse.persistence.testing.jaxb.typevariable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="root")
public class ExtendedList8Root implements ExtendedList8Interface {

        public ExtendedList8<ExtendedList8Root> foo;

        @Override
        public boolean equals(Object obj) {

            if(null == obj || obj.getClass() != this.getClass()) {
                return false;
            }
            ExtendedList8Root test = (ExtendedList8Root) obj;
            if(null == foo) {
                return null == test.foo;
            } else {
                return foo.equals(test.foo);
            }
        }
    }
