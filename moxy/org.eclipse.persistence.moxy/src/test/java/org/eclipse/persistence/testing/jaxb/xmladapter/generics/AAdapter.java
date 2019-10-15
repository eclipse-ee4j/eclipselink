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
// Matt MacIvor - July 4th 2011
package org.eclipse.persistence.testing.jaxb.xmladapter.generics;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class AAdapter<T> extends XmlAdapter<T, A<T>> {

    @Override
    public A<T> unmarshal(T v) throws Exception {
        A<T> a = new A<T>();
        a.t = v;
        return a;
    }

    @Override
    public T marshal(A<T> v) throws Exception {
        return v.t;
    }

}
