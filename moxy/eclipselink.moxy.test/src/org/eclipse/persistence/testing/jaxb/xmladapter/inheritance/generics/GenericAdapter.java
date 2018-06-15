/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 30 August 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.inheritance.generics;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class GenericAdapter<T extends Linkable> extends XmlAdapter<T, T> {

    private Map<String, T> cache = new HashMap<String, T>();

    public void cacheObject(T linkable) {
        cache.put(linkable.getHref(), linkable);
    }

    @Override
    public T marshal(T v) throws Exception {
        return v;
    }

    @Override
    public T unmarshal(T linkable) throws Exception {
        if(null == linkable.getHref()) {
            return linkable;
        }
        return cache.get(linkable.getHref());
    }

}
