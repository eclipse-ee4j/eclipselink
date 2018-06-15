/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Blaise Doughan - 2.5.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.typevariable;

public class FooImpl implements FooInterface{

    public Integer id;

    public FooImpl() {
    }

    public FooImpl(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        FooImpl test = (FooImpl) obj;
        return id == test.id;
    }

    @Override
    public int hashCode() {
        return 7;
    }

}
