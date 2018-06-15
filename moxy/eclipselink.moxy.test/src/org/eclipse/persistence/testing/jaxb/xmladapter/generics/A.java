/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Matt MacIvor - July 4th 2011
package org.eclipse.persistence.testing.jaxb.xmladapter.generics;

public class A<T> {

    public T t;

    public boolean equals(Object obj) {
        if(!(obj instanceof A)) {
            return false;
        }
        return t.equals(((A)obj).t);
    }

}
