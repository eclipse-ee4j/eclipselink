/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Martin Vojtek - initial implementation
 ******************************************************************************/
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
