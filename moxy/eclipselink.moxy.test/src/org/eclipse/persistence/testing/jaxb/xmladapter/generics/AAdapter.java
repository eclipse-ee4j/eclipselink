/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Matt MacIvor - July 4th 2011
 ******************************************************************************/
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
