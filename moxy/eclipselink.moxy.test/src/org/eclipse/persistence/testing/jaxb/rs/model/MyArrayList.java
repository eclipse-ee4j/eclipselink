/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - initial implementation
package org.eclipse.persistence.testing.jaxb.rs.model;

import java.util.ArrayList;
import java.util.Collection;

public class MyArrayList<T> extends ArrayList<T> {

    public MyArrayList() {
    }

    public MyArrayList(final Collection<T> a) {
        super(a);
    }

}
